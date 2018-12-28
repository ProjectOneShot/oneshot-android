package application.oneshot.constants;

public class Database {
    // Database
    public static final String DATABASE_CONTACTS = "contacts.db";
    public static final int DATABASE_CONTACTS_VERSION = 1;

    // Tables
    public static final String TABLE_CONTACTS = "contacts";

    // Columns
    public static final String CONTACTS_COLUMN_ID = "_id";
    public static final String CONTACTS_COLUMN_UID = "uid";
    public static final String CONTACTS_COLUMN_ALIAS = "alias";

    // Queries
    public static final String DATABASE_RECIPIENTS_CREATE =
            "create table " + TABLE_CONTACTS + "(" + CONTACTS_COLUMN_ID + " integer primary key autoincrement, "
                    + CONTACTS_COLUMN_UID + " text not null unique, " + CONTACTS_COLUMN_ALIAS + " text not null);";

    public static final String DATABASE_CONTACTS_DROP = "drop table if exists " + TABLE_CONTACTS;
}
