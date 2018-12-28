package application.oneshot.helpers;

import android.util.Base64;

import com.google.firebase.database.DataSnapshot;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.inject.Inject;
import javax.inject.Singleton;

import application.oneshot.beans.AesCipher;
import application.oneshot.beans.Conversation;
import application.oneshot.constants.Cryptography;
import application.oneshot.dataaccess.ContactDataAccess;
import application.oneshot.models.Contact;
import application.oneshot.models.Message;
import application.oneshot.models.User;

@Singleton
public class MessageHelper {

    @Inject
    AesHelper mAesHelper;
    @Inject
    ContactDataAccess mContactDataAccess;
    @Inject
    RsaHelper mRsaHelper;

    @Inject
    MessageHelper() {
    }

    public Conversation decryptUnreadMessage(DataSnapshot dataSnapshot)
            throws GeneralSecurityException, IOException {

        mContactDataAccess.open();

        final Message message = dataSnapshot.getValue(Message.class);

        final Message decryptedMessage = new Message();

        byte[] decryptedSecretKeyBytes = mRsaHelper.decrypt(message.getSecretKey());

        byte[] decryptedIvBytes = mRsaHelper.decrypt(message.getIv());

        final String decryptedSubject = new String(mAesHelper.decrypt(decryptedSecretKeyBytes, decryptedIvBytes,
                Base64.decode(message.getSubject(), Base64.URL_SAFE)));

        final String decryptedContent = new String(mAesHelper.decrypt(decryptedSecretKeyBytes, decryptedIvBytes,
                Base64.decode(message.getContent(), Base64.URL_SAFE)));

        decryptedMessage.setKey(dataSnapshot.getKey());
        decryptedMessage.setSender(message.getSender());
        decryptedMessage.setSubject(decryptedSubject);
        decryptedMessage.setContent(decryptedContent);
        decryptedMessage.setCreated(message.getCreated());
        decryptedMessage.setRead(message.isRead());

        // TODO: Cache (populate list with all contacts and search in that list)?
        // TODO: Might not be the best solution.
        Contact contact = mContactDataAccess.getByUid(message.getSender());

        if (contact == null) {
            contact = new Contact();
            contact.setUid(message.getRecipient());
        }

        mContactDataAccess.close();

        final Conversation conversation = new Conversation();
        conversation.setContact(contact);
        conversation.setMessage(decryptedMessage);
        return conversation;
    }

    public Message encryptMessage(User user, Message message)
            throws GeneralSecurityException {

        // Generate new AES cipher for each message.
        final AesCipher aesCipher = mAesHelper.generateCipher();

        // Encrypt message with generated AES key.
        final String subject = message.getSubject();
        byte[] subjectBytes = subject.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedSubjectBytes = mAesHelper.encrypt(aesCipher.getIv(), aesCipher.getSecretKey(), subjectBytes);
        final String encryptedSubjectBytesBase64 = new String(Base64.encode(encryptedSubjectBytes, Base64.URL_SAFE));

        final String content = message.getContent();
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedContentBytes = mAesHelper.encrypt(aesCipher.getIv(), aesCipher.getSecretKey(), contentBytes);
        final String encryptedContentBytesBase64 = new String(Base64.encode(encryptedContentBytes, Base64.URL_SAFE));

        // Encrypt AES key with recipient's RSA public key.
        byte[] publicKeyBytesBase64 = Base64.decode(user.getPublicKey(), Base64.URL_SAFE);

        final KeyFactory keyFactory = KeyFactory.getInstance(Cryptography.RSA_INSTANCE);

        final KeySpec keySpec = new X509EncodedKeySpec(publicKeyBytesBase64);

        final PublicKey publicKey = keyFactory.generatePublic(keySpec);

        byte[] encryptedSecretKeyBytes = mRsaHelper.encrypt(aesCipher.getSecretKey()
                .getEncoded(), publicKey);
        final String encryptedSecretKeyBytesBase64 = new String(
                Base64.encode(encryptedSecretKeyBytes, Base64.URL_SAFE));

        byte[] encryptedIvBytes = mRsaHelper.encrypt(aesCipher.getIv(), publicKey);
        final String encryptedIvBytesBase64 = new String(Base64.encode(encryptedIvBytes, Base64.URL_SAFE));

        // Create new message.
        message.setSubject(encryptedSubjectBytesBase64);
        message.setContent(encryptedContentBytesBase64);
        message.setCreated(System.currentTimeMillis());
        message.setSecretKey(encryptedSecretKeyBytesBase64);
        message.setIv(encryptedIvBytesBase64);

        return message;
    }
}
