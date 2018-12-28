package application.oneshot;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;

import javax.inject.Inject;

import application.oneshot.adapters.ContactsArrayAdapter;
import application.oneshot.beans.Conversation;
import application.oneshot.constants.Extras;
import application.oneshot.constants.Firebase;
import application.oneshot.constants.Preferences;
import application.oneshot.dataaccess.ContactDataAccess;
import application.oneshot.fragments.AlertDialogFragment;
import application.oneshot.helpers.MessageHelper;
import application.oneshot.helpers.SharedPreferencesHelper;
import application.oneshot.models.Contact;
import application.oneshot.models.Message;
import application.oneshot.models.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class ComposeActivity
        extends BaseActivity
        implements AlertDialogFragment.AlertDialogListener {

    @Inject
    ContactDataAccess mContactDataAccess;
    @Inject
    DatabaseReference mDatabaseReference;
    @Inject
    MessageHelper mMessageHelper;
    @Inject
    SharedPreferencesHelper mSharedPreferencesHelper;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.auto_complete_edit_text_recipient)
    AutoCompleteTextView editTextRecipient;
    @BindView(R.id.edit_text_subject)
    EditText editTestSubject;
    @BindView(R.id.edit_text_message)
    EditText editTextMessage;

    private ValueEventListener mSingleValueEventListener = createSingleValueEventListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_compose);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Conversation conversation = (Conversation) getIntent().getSerializableExtra(Extras.INTENT_EXTRA);

        if (conversation != null) {
            editTextRecipient.setText(conversation.getMessage()
                    .getSender());

            editTestSubject.setText(String.format(getString(R.string.reply_edit_text_subject), conversation.getMessage()
                    .getSubject()));

            editTextMessage.setText(String.format(getString(R.string.reply_edit_text_message),
                    DateFormat.getDateTimeInstance()
                            .format(conversation.getMessage()
                                    .getCreated()), conversation.getMessage()
                            .getSender(), conversation.getMessage()
                            .getContent()));
        } else {
            final Uri uri = getIntent().getData();

            if (uri != null) {
                editTextRecipient.setText(uri.getQueryParameter("recipient"));
            }
        }

        editTextRecipient.setOnItemClickListener(createOnItemClickListener());
    }

    @Override
    protected void onPause() {
        super.onPause();

        mDatabaseReference.removeEventListener(mSingleValueEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        (new BindArrayAdapter()).execute();
    }

    @Override
    public void onBackPressed() {
        if (mSharedPreferencesHelper.get(Preferences.CONFIRM_MESSAGE_DISCARD,
                Preferences.CONFIRM_MESSAGE_DISCARD_DEFAULT)) {

            showDialog(getString(R.string.alert_dialog_compose_discard), 1);
        } else {
            NavUtils.navigateUpFromSameTask(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_compose_options_menu, menu);

        return true;
    }

    @Override
    public void onDialogNegativeClick(int callback) {
    }

    @Override
    public void onDialogPositiveClick(int callback) {
        if (callback == 1) {
            NavUtils.navigateUpFromSameTask(this);
        } else {
            send();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();

            return true;
        } else if (id == R.id.action_send) {
            if (mSharedPreferencesHelper.get(Preferences.CONFIRM_MESSAGE_SEND,
                    Preferences.CONFIRM_MESSAGE_SEND_DEFAULT)) {

                showDialog(getString(R.string.alert_dialog_compose_send));
            } else {
                send();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //@OnItemClick(R.id.auto_complete_edit_text_recipient)
    private AdapterView.OnItemClickListener createOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = (Contact) parent.getAdapter()
                        .getItem(position);

                editTextRecipient.setText(contact.getUid());
            }
        };
    }

    private ValueEventListener createSingleValueEventListener() {
        return new ValueEventListener() {
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dismissProgressDialog();

                Toast.makeText(ComposeActivity.this, R.string.toast_error, Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    // Fetch recipient's public RSA key.
                    final User user = dataSnapshot.getValue(User.class);

                    if (user == null || user.getPublicKey() == null) {
                        Toast.makeText(ComposeActivity.this, R.string.toast_error, Toast.LENGTH_LONG)
                                .show();
                    } else {
                        final Message plaintextMessage = new Message();
                        // TODO: Sender UID might already exist somewhere in the context.
                        plaintextMessage.setSender(mSharedPreferencesHelper.get(Preferences.UID, null));
                        plaintextMessage.setRecipient(String.valueOf(editTextRecipient.getText()));
                        plaintextMessage.setSubject(String.valueOf(editTestSubject.getText()));
                        plaintextMessage.setContent(String.valueOf(editTextMessage.getText()));

                        final Message encryptedMessage = mMessageHelper.encryptMessage(user, plaintextMessage);

                        // Create new Firebase message node.
                        mDatabaseReference.child(Firebase.DATABASE_NODE_API)
                                .child(Firebase.DATABASE_NODE_MESSAGES)
                                .push()
                                .setValue(encryptedMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                dismissProgressDialog();

                NavUtils.navigateUpFromSameTask(ComposeActivity.this);
            }
        };
    }

    private void send() {
        showProgressDialog(R.string.progress_dialog_sending);

        mDatabaseReference.child(Firebase.DATABASE_NODE_API)
                .child(Firebase.DATABASE_NODE_USERS)
                .child(String.valueOf(editTextRecipient.getText()))
                .addListenerForSingleValueEvent(mSingleValueEventListener);
    }

    private void showDialog(String message) {
        final AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(message);
        alertDialogFragment.show(getFragmentManager(), Extras.INTENT_EXTRA);
    }

    private void showDialog(String message, int callback) {
        final AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(message, callback);
        alertDialogFragment.show(getFragmentManager(), Extras.INTENT_EXTRA);
    }

    private class BindArrayAdapter
            extends AsyncTask<String, Void, ArrayList<Contact>> {

        @Override
        protected ArrayList<Contact> doInBackground(String... strings) {
            mContactDataAccess.open();

            return mContactDataAccess.getAll();
        }

        @Override
        protected void onPostExecute(ArrayList<Contact> contacts) {
            super.onPostExecute(contacts);

            final ContactsArrayAdapter contactsArrayAdapter = new ContactsArrayAdapter(ComposeActivity.this, contacts);

            editTextRecipient.setAdapter(contactsArrayAdapter);

            mContactDataAccess.close();
        }
    }
}
