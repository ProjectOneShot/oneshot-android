package application.oneshot;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import javax.inject.Inject;

import application.oneshot.beans.Conversation;
import application.oneshot.constants.Extras;
import application.oneshot.constants.Firebase;
import application.oneshot.constants.Preferences;
import application.oneshot.helpers.SharedPreferencesHelper;
import application.oneshot.models.Message;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnLongClick;
import dagger.android.AndroidInjection;

public class ReadActivity
        extends BaseActivity {

    @Inject
    DatabaseReference mDatabaseReference;
    @Inject
    SharedPreferencesHelper mSharedPreferencesHelper;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.text_view_sender)
    TextView editTextSender;
    @BindView(R.id.text_view_subject)
    TextView editTextSubject;
    @BindView(R.id.text_view_content)
    TextView editTextContent;

    private Conversation mConversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_read);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mConversation = (Conversation) getIntent().getSerializableExtra(Extras.INTENT_EXTRA);

        if (mConversation.getContact()
                .getAlias() == null) {
            editTextSender.setText(mConversation.getContact()
                    .getUid());
        } else {
            editTextSender.setText(mConversation.getContact()
                    .getAlias());
        }

        editTextSubject.setText(mConversation.getMessage()
                .getSubject());
        editTextContent.setText(mConversation.getMessage()
                .getContent());

        final Message message = mConversation.getMessage();

        if (mSharedPreferencesHelper.get(Preferences.DELETE_READ_MESSAGES, Preferences.DELETE_READ_MESSAGES_DEFAULT)) {

            mDatabaseReference.child(Firebase.DATABASE_NODE_API)
                    .child(Firebase.DATABASE_NODE_MESSAGES)
                    .child(message.getKey())
                    .removeValue();
        } else {
            mDatabaseReference.child(Firebase.DATABASE_NODE_API)
                    .child(Firebase.DATABASE_NODE_MESSAGES)
                    .child(message.getKey())
                    .child("read")
                    .setValue(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_read_options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.action_reply) {
            final Intent intent = new Intent(this, ComposeActivity.class);
            intent.putExtra(Extras.INTENT_EXTRA, mConversation);

            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnLongClick(R.id.text_view_content)
    public boolean onTextViewContentLongClick() {
        setClipboardClip(mConversation.getMessage()
                .getContent());

        return true;
    }

    @OnLongClick(R.id.text_view_sender)
    public boolean onTextViewSenderLongClick() {
        setClipboardClip(mConversation.getContact()
                .getUid());

        return false;
    }

    @OnLongClick(R.id.text_view_subject)
    public boolean onTextViewSubjectLongClick() {
        setClipboardClip(mConversation.getMessage()
                .getSubject());

        return true;
    }

    private void setClipboardClip(String text) {
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        final CharSequence label = getResources().getText(R.string.application_label);

        final ClipData clipData = ClipData.newPlainText(label, text);

        clipboardManager.setPrimaryClip(clipData);

        Toast.makeText(this, R.string.toast_copied_to_clipboard, Toast.LENGTH_LONG)
                .show();
    }
}
