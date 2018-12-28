package application.oneshot.beans;

import javax.crypto.SecretKey;

public class AesCipher {
    private byte[] mIv;
    private SecretKey mSecretKey;

    public AesCipher(byte[] iv, SecretKey secretKey) {
        mIv = iv;
        mSecretKey = secretKey;
    }

    public byte[] getIv() {
        return mIv;
    }

    public void setIv(byte[] iv) {
        mIv = iv;
    }

    public SecretKey getSecretKey() {
        return mSecretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        mSecretKey = secretKey;
    }
}
