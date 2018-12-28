package application.oneshot.helpers;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.inject.Inject;
import javax.inject.Singleton;

import application.oneshot.constants.Cryptography;

@Singleton
public class RsaHelper {

    @Inject
    RsaHelper(Context context) {
    }

    byte[] decrypt(String input)
            throws GeneralSecurityException, IOException {

        byte[] bytes = Base64.decode(input, Base64.URL_SAFE);

        return decrypt(bytes);
    }

    public void deleteKeyStore()
            throws GeneralSecurityException, IOException {

        final KeyStore keyStore = KeyStore.getInstance(Cryptography.KEY_STORE_INSTANCE);
        keyStore.load(null);

        if (keyStore.containsAlias(Cryptography.KEY_STORE_ALIAS)) {
            keyStore.deleteEntry(Cryptography.KEY_STORE_ALIAS);
        }
    }

    byte[] encrypt(byte[] input, PublicKey publicKey)
            throws GeneralSecurityException {

        final OAEPParameterSpec oaepParameterSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1,
                PSource.PSpecified.DEFAULT);

        final Cipher cipher = Cipher.getInstance(Cryptography.RSA_CIPHER_INSTANCE);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParameterSpec);
        return cipher.doFinal(input);
    }

    public PublicKey getOrSetPublicKey()
            throws GeneralSecurityException, IOException {

        return getOrSetKeyPair().getPublic();
    }

    private byte[] decrypt(byte[] input)
            throws GeneralSecurityException, IOException {

        final OAEPParameterSpec oaepParameterSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1,
                PSource.PSpecified.DEFAULT);

        final Cipher cipher = Cipher.getInstance(Cryptography.RSA_CIPHER_INSTANCE);
        cipher.init(Cipher.DECRYPT_MODE, getOrSetPrivateKey(), oaepParameterSpec);
        return cipher.doFinal(input);
    }

    private void generateKeyPair(String alias)
            throws GeneralSecurityException {

        final AlgorithmParameterSpec algorithmParameterSpec = new KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setEncryptionPaddings(
                KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA1)
                .setKeySize(Cryptography.RSA_KEY_SIZE)
                .build();

        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(Cryptography.RSA_INSTANCE,
                Cryptography.KEY_STORE_INSTANCE);
        keyPairGenerator.initialize(algorithmParameterSpec);
        keyPairGenerator.generateKeyPair();
    }

    private KeyPair getOrSetKeyPair()
            throws GeneralSecurityException, IOException {

        final KeyStore keyStore = KeyStore.getInstance(Cryptography.KEY_STORE_INSTANCE);
        keyStore.load(null);

        if (!keyStore.containsAlias(Cryptography.KEY_STORE_ALIAS)) {
            generateKeyPair(Cryptography.KEY_STORE_ALIAS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            final PublicKey publicKey = keyStore.getCertificate(Cryptography.KEY_STORE_ALIAS)
                    .getPublicKey();
            final PrivateKey privateKey = (PrivateKey) keyStore.getKey(Cryptography.KEY_STORE_ALIAS, null);

            return new KeyPair(publicKey, privateKey);
        }

        final KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(
                Cryptography.KEY_STORE_ALIAS, null);

        final PublicKey publicKey = privateKeyEntry.getCertificate()
                .getPublicKey();
        final PrivateKey privateKey = privateKeyEntry.getPrivateKey();

        return new KeyPair(publicKey, privateKey);
    }

    private PrivateKey getOrSetPrivateKey()
            throws GeneralSecurityException, IOException {

        return getOrSetKeyPair().getPrivate();
    }
}
