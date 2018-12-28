package application.oneshot.helpers;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Singleton;

import application.oneshot.beans.AesCipher;
import application.oneshot.constants.Cryptography;

@Singleton
public class AesHelper {

    @Inject
    AesHelper() {
    }

    byte[] decrypt(byte[] secretKeySpec, byte[] iv, byte[] input)
            throws GeneralSecurityException {

        final SecretKey secretKey = new SecretKeySpec(secretKeySpec, Cryptography.AES_INSTANCE);

        final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(Cryptography.GCM_TAG_LENGTH, iv);

        final Cipher cipher = Cipher.getInstance(Cryptography.AES_CIPHER_INSTANCE);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        return cipher.doFinal(input);
    }

    byte[] encrypt(byte[] iv, SecretKey secretKey, byte[] input)
            throws GeneralSecurityException {

        final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(Cryptography.GCM_TAG_LENGTH, iv);

        final Cipher cipher = Cipher.getInstance(Cryptography.AES_CIPHER_INSTANCE);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
        return cipher.doFinal(input);
    }

    AesCipher generateCipher()
            throws GeneralSecurityException {

        final KeyGenerator keyGenerator = KeyGenerator.getInstance(Cryptography.AES_INSTANCE);
        keyGenerator.init(Cryptography.AES_KEY_SIZE);

        final SecretKey secretKey = keyGenerator.generateKey();

        final byte[] iv = new byte[Cryptography.GCM_RANDOM_SEED_SIZE];
        final SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        return new AesCipher(iv, secretKey);
    }
}
