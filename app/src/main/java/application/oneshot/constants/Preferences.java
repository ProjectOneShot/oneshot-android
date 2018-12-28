package application.oneshot.constants;

public class Preferences {
    public static final String SHARED_PREFERENCE = "android.oneshot.preferences";

    // Keys
    public static final String UID = "uid";
    public static final String INTRODUCTION = "show_introduction";

    public static final String CONFIRM_DEVICE_CREDENTIALS = "confirm_device_credentials";
    public static final String CONFIRM_MESSAGE_SEND = "confirm_message_send";
    public static final String CONFIRM_MESSAGE_DISCARD = "confirm_message_discard";
    public static final String USE_NIGHT_MODE = "use_night_mode";
    public static final String DELETE_READ_MESSAGES = "delete_read_messages";

    public static final boolean INTRODUCTION_DEFAULT = true;

    public static final boolean CONFIRM_DEVICE_CREDENTIALS_DEFAULT = false;
    public static final boolean CONFIRM_MESSAGE_SEND_DEFAULT = true;
    public static final boolean CONFIRM_MESSAGE_DISCARD_DEFAULT = true;
    public static final String USE_NIGHT_MODE_DEFAULT = "2";
    public static final boolean DELETE_READ_MESSAGES_DEFAULT = true;
}
