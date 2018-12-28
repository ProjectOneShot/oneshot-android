package application.oneshot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.inject.Inject;

import application.oneshot.constants.Firebase;
import application.oneshot.constants.Preferences;
import application.oneshot.helpers.RsaHelper;
import application.oneshot.helpers.SharedPreferencesHelper;
import application.oneshot.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class AuthActivity
        extends BaseActivity {

    @Inject
    DatabaseReference mDatabaseReference;
    @Inject
    FirebaseAuth mFirebaseAuth;
    @Inject
    RsaHelper mRsaHelper;
    @Inject
    SharedPreferencesHelper mSharedPreferencesHelper;

    @BindView(R.id.text_view)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        ButterKnife.bind(this);

        mFirebaseAuth.addAuthStateListener(createAuthStateChangedListener());
        mFirebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, createOnCompleteListener());

        showProgressDialog(R.string.progress_dialog_loading);
    }

    private FirebaseAuth.AuthStateListener createAuthStateChangedListener() {
        return new FirebaseAuth.AuthStateListener() {
            // This method gets called (on the UI thread) on
            // any change in the Firebase authentication state.
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // TODO:
                // Update user if the app has been restored from the backup,
                // presumably if anonymous auth identifier hasn't changed or has
                // been backed up itself.

                // Account exists, update the user (for ex. FCM token).
                if (firebaseAuth.getCurrentUser() != null) {
                    createOrUpdateUser(firebaseAuth.getCurrentUser()
                            .getUid());
                }
            }
        };
    }

    private OnCompleteListener<AuthResult> createOnCompleteListener() {
        return new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final String uid = task.getResult()
                            .getUser()
                            .getUid();

                    String cachedUid = mSharedPreferencesHelper.get(Preferences.UID, null); // Default to uid

                    // Create new Firebase user node.
                    if (cachedUid == null /*|| !cachedUid.equals(uid)*/) {
                        // Cache user identifier.
                        mSharedPreferencesHelper.put(Preferences.UID, uid);

                        // Account does not exist, create a new user.
                        createOrUpdateUser(uid);
                    }

                    startActivity(new Intent(AuthActivity.this, MailboxActivity.class));

                    finish();
                } else {
                    Toast.makeText(AuthActivity.this, R.string.toast_error, Toast.LENGTH_LONG)
                            .show();
                }

                dismissProgressDialog();
            }
        };
    }

    private void createOrUpdateUser(final String uid) {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        try {
                            final String token = instanceIdResult.getToken();

                            final String rsaPublicKeyBase64 = new String(Base64.encode(mRsaHelper.getOrSetPublicKey()
                                    .getEncoded(), Base64.URL_SAFE));

                            final User user = new User();
                            user.setPublicKey(rsaPublicKeyBase64);
                            user.setUserToken(token);

                            mDatabaseReference.child(Firebase.DATABASE_NODE_API)
                                    .child(Firebase.DATABASE_NODE_USERS)
                                    .child(uid)
                                    .setValue(user);
                        } catch (GeneralSecurityException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
