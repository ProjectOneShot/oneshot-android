package application.oneshot.constants;

public class Cryptography {
    public static final String KEY_STORE_ALIAS = "ONE_SHOT";
    public static final String KEY_STORE_INSTANCE = "AndroidKeyStore";

    public static final String RSA_INSTANCE = "RSA";
    public static final String RSA_CIPHER_INSTANCE = "RSA/NONE/OAEPPadding";
    public static final int RSA_KEY_SIZE = 4096;

    public static final String AES_INSTANCE = "AES";
    public static final String AES_CIPHER_INSTANCE = "AES/GCM/NoPadding";
    public static final int AES_KEY_SIZE = 256;
//    public static final int AES_RANDOM_SEED_SIZE = 16; // = 128 bits

    public static final int GCM_TAG_LENGTH = 128;
    public static final int GCM_RANDOM_SEED_SIZE = 12; // Only 12-byte (96-bit) long IVs supported.
}
