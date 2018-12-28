package application.oneshot.dataaccess;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import application.oneshot.constants.Database;
import application.oneshot.helpers.ContactDatabaseHelper;
import application.oneshot.models.Contact;

@Singleton
public class ContactDataAccess {

    private final String[] COLUMNS =
            {Database.CONTACTS_COLUMN_ID, Database.CONTACTS_COLUMN_UID, Database.CONTACTS_COLUMN_ALIAS};

    @Inject
    ContactDatabaseHelper mContactDatabaseHelper;

    private SQLiteDatabase mSQLiteDatabase;

    @Inject
    public ContactDataAccess() {
    }

    public void close() {
        mContactDatabaseHelper.close();
    }

    public Contact create(String uid, String alias) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(Database.CONTACTS_COLUMN_UID, uid);
        contentValues.put(Database.CONTACTS_COLUMN_ALIAS, alias);

        final long id = mSQLiteDatabase.insert(Database.TABLE_CONTACTS, null, contentValues);

        final Cursor cursor = mSQLiteDatabase.query(Database.TABLE_CONTACTS, COLUMNS,
                Database.CONTACTS_COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();

        // TODO: Handle exceptions.
        if (cursor.getCount() == 0) {
            cursor.close();

            return null;
        }

        final Contact contact = new Contact();
        contact.setId(cursor.getLong(0));
        contact.setUid(cursor.getString(1));
        contact.setAlias(cursor.getString(2));

        cursor.close();

        return contact;
    }

    public void delete(Contact contact) {
        final long id = contact.getId();

        mSQLiteDatabase.delete(Database.TABLE_CONTACTS, Database.CONTACTS_COLUMN_ID + " = " + id, null);
    }

    public boolean exists(String[] selectionArgs) {
        final Cursor cursor = mSQLiteDatabase.query(Database.TABLE_CONTACTS, COLUMNS,
                Database.CONTACTS_COLUMN_UID + " = ?", selectionArgs, null, null, null, null);
        cursor.moveToFirst();

        final boolean exists = (cursor.getCount() > 0);

        cursor.close();

        return exists;
    }

    public ArrayList<Contact> getAll() {
        final ArrayList<Contact> contacts = new ArrayList<>();

        final Cursor cursor = mSQLiteDatabase.query(Database.TABLE_CONTACTS, COLUMNS, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Contact contact = new Contact();
            contact.setId(cursor.getLong(0));
            contact.setUid(cursor.getString(1));
            contact.setAlias(cursor.getString(2));

            contacts.add(contact);

            cursor.moveToNext();
        }

        cursor.close();

        return contacts;
    }

    public Contact getByUid(String uid) {
        final Cursor cursor = mSQLiteDatabase.query(Database.TABLE_CONTACTS, COLUMNS,
                Database.CONTACTS_COLUMN_UID + " = ?", new String[]{uid}, null, null, null, null);
        cursor.moveToFirst();

        // TODO: Handle exceptions.
        if (cursor.getCount() == 0) {
            cursor.close();

            return null;
        }

        final Contact contact = new Contact();
        contact.setId(cursor.getLong(0));
        contact.setUid(cursor.getString(1));
        contact.setAlias(cursor.getString(2));

        cursor.close();

        return contact;
    }

    public void open()
            throws SQLException {

        mSQLiteDatabase = mContactDatabaseHelper.getWritableDatabase();
    }

    public void update(Contact contact) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(Database.CONTACTS_COLUMN_UID, contact.getUid());
        contentValues.put(Database.CONTACTS_COLUMN_ALIAS, contact.getAlias());

        mSQLiteDatabase.update(Database.TABLE_CONTACTS, contentValues,
                Database.CONTACTS_COLUMN_ID + " = " + contact.getId(), null);
    }
}
