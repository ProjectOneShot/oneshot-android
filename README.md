# OneShot

OneShot is a low ceremony Android text messaging app with focus on privacy and end-to-end security.

Information how to fork and "host" your own build is available [below](#fork). If you're looking for a more feature complete app, please consider using [Signal](https://signal.org/).

## End-to-end Encryption

When initialized, app will generate a [RSA](<https://en.wikipedia.org/wiki/RSA_(cryptosystem)>) key-pair (RSA/NONE/OAEPPadding) using [Android Keystore](https://developer.android.com/training/articles/keystore), create an anonymous user account using Firebase Authentication and a Firebase Cloud Messaging registration token. Randomly generated user identifer and registration token will be downloaded to the device. Meanwhile, RSA public key will be uploaded to the Firebase Realtime Database and associated with the authenticated user.

Prior to sending a message, sender must know recipient's anonymous identifer.

Each time a new message is created, it will be encrypted using a freshly generated 256-bit [AES](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard) key (AES/GCM/NoPadding). Before sending the message, app will fetch the recipient's public RSA key from the cloud database and use it to encrypt the AES key. Afterwards, both the encrypted AES key and the encrypted message will be stored in the cloud database.

Firebase Cloud Messaging service will then be used to notify the intended recipient of incoming message.

When reading an encrypted message, recipient's private RSA key - securely stored on recipient's device - is used to decrypt the AES key used for encrypting the original message. Once decrypted itself, AES key will be used to decrypt the contents of the message.

## Metadata

OneShot was built with anonymity, privacy and security in mind, but it does not come with 100% guarantee. Although all communication between parties is fully encrypted with algorithms considered secure at the time of writing, and parties behind the communication are directly unknown, certain data - called metadata - can potentially lead to identifying one or all parties engaged in communication.

OneShot uses Firebase BaaS which is operated by Google. With that said, I leave the rest to your imagination. Data that Google can indirectly use to identify the parties are Firebase Authentication anonymous user identity, as well as Firebase Cloud Messaging registration token.

To use OneShot, users are not required to sign in on their phones using Google account.

## Fork

Minimum understanding of Android app development - and software development in general - is required to fork and build this project.

1. Set up Git [❔](https://help.github.com/articles/set-up-git/)
2. Clone this repository [❔](https://help.github.com/articles/cloning-a-repository-from-github/)
3. Connect the app to Firebase [❔](https://firebase.google.com/docs/android/setup#manually_add_firebase)
   1. Enable anonymous Sign-in provider for the Firebase Authentication
      - In the Firebase console [🔗](https://console.firebase.google.com/), open the **Authentication** service
      - On the **Sign-in method** tab, enable the **Anonymous** sign-in method
   2. Set up the Firebase Realtime Database
      - In the Firebase console [🔗](https://console.firebase.google.com/), open the **Database** service
      - Create new Firebase Realtime Database
      - Set up rules on the **Rules** tab
        - Example rules are available [below](#firebase-realtime-database-rules)
   3. Create a Function for sending Firebase Cloud Messaging notifications [❔](https://firebase.google.com/docs/functions/get-started#set-up-nodejs-and-the-firebase-cli)
      - Example function is available [below](#firebase-realtime-database-triggers)
4. Set up Android Studio [❔](https://developer.android.com/studio/install)
   - Install Android 9 SDK (API level 28), as well as Android Build Tools 28.0.3
5. Change the default app package name to avoid package conflicts [❔](https://www.jetbrains.com/help/idea/rename-refactorings.html)
6. Build the project using Android Studio, or manually using [Gradle](https://gradle.org/) as described [below](#gradle-build)

### Firebase Realtime Database Rules

```json
{
  "rules": {
    "v1": {
      ".read": "auth.uid != null",
      ".write": "auth.uid != null",
      "messages": {
        "$message": {
          ".read": "auth.uid != null && query.orderByChild == 'recipient' && query.equalTo == auth.uid",
          ".write": "auth.uid != null && query.orderByChild == 'recipient' && query.equalTo == auth.uid"
        },
        ".indexOn": "recipient"
      },
      "users": {
        "userToken": {
          ".read": false
        }
      }
    }
  }
}
```

### Firebase Realtime Database Triggers

```js
"use strict";

const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendNotification = functions.database
  .ref("/v1/messages/{message}")
  .onCreate((snapshot, context) => {
    const message = context.params.message;
    const recipient = snapshot.child("recipient").val();

    console.log(
      `Message "${message}" has been created for recipient "${recipient}."`
    );

    return admin
      .database()
      .ref(`/v1/users/${recipient}/userToken`)
      .once("value")
      .then(snapshot => {
        const registrationToken = snapshot.val();

        const payload = {
          notification: {
            title: "TITLE",
            body: "BODY"
          }
        };

        return admin.messaging().sendToDevice(registrationToken, payload);
      })
      .then(response => {
        response.results.forEach((result, index) => {
          const error = result.error;

          if (error) {
            return console.error(
              `An error occurred while processing message for recipient: ${error.toJSON()}`
            );
          } else {
            return console.log(
              `Message "${result.messageId}" successfully processed.`
            );
          }
        });

        return console.log(
          `Successfully processed and sent ${response.successCount} message(s).`
        );
      });
  });
```

## Gradle Build

You can execute the build tasks manually using the [Gradle wrapper](https://developer.android.com/studio/build/building-cmdline) command line tool. Before you do, please make sure you have installed Android 9 SDK (API level 28), as well as Android Build Tools 28.0.3.

## Contributions

We encourage contributions, both issues and pull requests.

### Coding Style

OneShot coding style for Android Studio is available in `/ide/CodeStyle.xml`.

## License

OneShot is licensed under the MIT license.
