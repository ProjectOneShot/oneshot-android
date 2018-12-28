package application.oneshot.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.inject.Inject;
import javax.inject.Singleton;

import application.oneshot.constants.Database;

@Singleton
public class ContactDatabaseHelper
        extends SQLiteOpenHelper /*implements BaseColumns*/ {

    @Inject
    public ContactDatabaseHelper(Context context) {
        super(context, Database.DATABASE_CONTACTS, null, Database.DATABASE_CONTACTS_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Database.DATABASE_RECIPIENTS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Database.DATABASE_CONTACTS_DROP);

        onCreate(db);
    }
}
