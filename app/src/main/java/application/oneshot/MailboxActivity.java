package application.oneshot;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.inject.Inject;

import application.oneshot.adapters.MessagesRecyclerViewAdapter;
import application.oneshot.beans.Conversation;
import application.oneshot.constants.Extras;
import application.oneshot.constants.Firebase;
import application.oneshot.constants.Preferences;
import application.oneshot.fragments.AlertDialogFragment;
import application.oneshot.fragments.QrCodeDialogFragment;
import application.oneshot.helpers.MessageHelper;
import application.oneshot.helpers.MessageRecyclerViewItemTouchHelper;
import application.oneshot.helpers.QrCodeHelper;
import application.oneshot.helpers.RsaHelper;
import application.oneshot.helpers.SharedPreferencesHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;

public class MailboxActivity
        extends BaseActivity
        implements AlertDialogFragment.AlertDialogListener, MessagesRecyclerViewAdapter.MessagesRecyclerViewAdapterBase,
        MessageRecyclerViewItemTouchHelper.MessageRecyclerViewItemTouchHelperBase,
        NavigationView.OnNavigationItemSelectedListener {

    @Inject
    DatabaseReference mDatabaseReference;
    @Inject
    FirebaseAuth mFirebaseAuth;
    @Inject
    MessageHelper mMessageHelper;
    @Inject
    QrCodeHelper mQrCodeHelper;
    @Inject
    RsaHelper mRsaHelper;
    @Inject
    SharedPreferencesHelper mSharedPreferencesHelper;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.text_view)
    TextView textView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    private ChildEventListener mChildEventListener;

    private MessagesRecyclerViewAdapter mMessagesRecyclerViewAdapter;

    private Query mQuery;

    private ValueEventListener mValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        // Preferences
        updatePreferences();

        setContentView(R.layout.activity_mailbox);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());

        recyclerView.addItemDecoration(dividerItemDecoration);

        final ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new MessageRecyclerViewItemTouchHelper(0,
                ItemTouchHelper.LEFT, this);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        stopListeningForMessages();
    }

    @Override
    public void onResume() {
        super.onResume();

        startListeningForMessages(mSharedPreferencesHelper.get(Preferences.UID, null));
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.nav_message_compose) {
            startActivity(new Intent(this, ComposeActivity.class));
        } else if (id == R.id.nav_message_contacts) {
            startActivity(new Intent(this, ContactsActivity.class));
        } else if (id == R.id.nav_account_reset) {
            showDialog(getString(R.string.alert_dialog_reset));
        } else if (id == R.id.nav_account_copy) {
            copyUid();
        } else if (id == R.id.nav_account_share) {
            shareUid();
        } else if (id == R.id.nav_account_qr_code_view) {
            viewQr();
        } else if (id == R.id.nav_account_qr_code_scan) {
            scanQr();
        } else if (id == R.id.nav_settings_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onItemClick(Conversation conversation) {
        final Intent intent = new Intent(this, ReadActivity.class);
        intent.putExtra(Extras.INTENT_EXTRA, conversation);

        startActivity(intent);
    }

    @Override
    public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof MessagesRecyclerViewAdapter.MessageRecyclerViewHolder) {
            final int adapterPosition = viewHolder.getAdapterPosition();

            final Conversation conversation = mMessagesRecyclerViewAdapter.getItem(adapterPosition);

            mMessagesRecyclerViewAdapter.removeItem(adapterPosition);

            mDatabaseReference.child(Firebase.DATABASE_NODE_API)
                    .child(Firebase.DATABASE_NODE_MESSAGES)
                    .child(conversation.getMessage()
                            .getKey())
                    .removeValue();
        }
    }

    @OnClick(R.id.fab)
    public void onClick(View view) {
        startActivity(new Intent(this, ComposeActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (intentResult != null && intentResult.getContents() != null) {
            // TODO: Start ComposeActivity instead?
            setClipboardClip(intentResult.getContents());
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onDialogNegativeClick(int callback) {
    }

    @Override
    public void onDialogPositiveClick(int callback) {
        reset();
    }

    private void bindRecyclerViewAdapter(DataSnapshot dataSnapshot)
            throws GeneralSecurityException, IOException {

        final Conversation conversations = mMessageHelper.decryptUnreadMessage(dataSnapshot);

        mMessagesRecyclerViewAdapter.addItem(conversations);
    }

    private void copyUid() {
        final String text = mSharedPreferencesHelper.get(Preferences.UID, null);

        setClipboardClip(text);
    }

    private ChildEventListener createChildEventListener() {
        return new ChildEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                // dataSnapshot = Message
                try {
                    bindRecyclerViewAdapter(dataSnapshot);
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
        };
    }

    private ValueEventListener createValueEventListener() {
        return new ValueEventListener() {
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    textView.setVisibility(View.GONE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                }

                progressBar.setVisibility(View.GONE);
            }
        };
    }

    // TODO: Move further back in the flow.
    // TODO: Wait for all tasks to complete.
    private void reset() {
        try {
            final String uid = mSharedPreferencesHelper.get(Preferences.UID, null);

            // TODO:
            // Consider flagging user as deleted and using Firebase Functions
            // to remove all references belonging to that user.

            // Remove all messages belonging to the signed-out user.
            mDatabaseReference.child(Firebase.DATABASE_NODE_API)
                    .child(Firebase.DATABASE_NODE_MESSAGES)
                    .orderByChild(Firebase.DATABASE_PROPERTY_MESSAGE_RECIPIENT)
                    .equalTo(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef()
                                    .setValue(null);
                        }
                    });

            // Remove signed-out user from the database.
            mDatabaseReference.child(Firebase.DATABASE_NODE_API)
                    .child(Firebase.DATABASE_NODE_USERS)
                    .child(uid)
                    .removeValue();

            mRsaHelper.deleteKeyStore();

            mSharedPreferencesHelper.remove(Preferences.UID);

            mFirebaseAuth.signOut();

            finish();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private void scanQr() {
        final IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt(getString(R.string.qr_code_scanner_prompt));
        integrator.initiateScan();
    }

    private void setClipboardClip(String text) {
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        final CharSequence label = getResources().getText(R.string.application_label);

        final ClipData clipData = ClipData.newPlainText(label, text);

        clipboardManager.setPrimaryClip(clipData);

        Toast.makeText(this, R.string.toast_account_id_copied_to_clipboard, Toast.LENGTH_LONG)
                .show();
    }

    private void shareUid() {
        final CharSequence template = getResources().getText(R.string.oneshot_share);

        final String uid = mSharedPreferencesHelper.get(Preferences.UID, null);

        final String text = String.format(template.toString(), uid);

        final Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");

        final CharSequence title = getResources().getText(R.string.intent_share_title);

        startActivity(Intent.createChooser(sendIntent, title));
    }

    private void showDialog(String message) {
        final AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(message);
        alertDialogFragment.show(getFragmentManager(), Extras.INTENT_EXTRA);
    }

    private void startListeningForMessages(String uid) {
        mMessagesRecyclerViewAdapter = new MessagesRecyclerViewAdapter(this);

        recyclerView.setAdapter(mMessagesRecyclerViewAdapter);

        mChildEventListener = createChildEventListener();
        mValueEventListener = createValueEventListener();

        // Get unread messages for the logged-on user.
        // Keep listening for new messages.
        mQuery = mDatabaseReference.child(Firebase.DATABASE_NODE_API)
                .child(Firebase.DATABASE_NODE_MESSAGES)
                .orderByChild(Firebase.DATABASE_PROPERTY_MESSAGE_RECIPIENT) // Has ".indexOn"
                .equalTo(uid);
        mQuery.addChildEventListener(mChildEventListener);
        mQuery.addValueEventListener(mValueEventListener);
    }

    // Any similarity with C - as in language - is purely coincidental.
    private void stopListeningForMessages() {
        mQuery.removeEventListener(mValueEventListener);
        mQuery.removeEventListener(mChildEventListener);
        mQuery = null;

        mValueEventListener = null;
        mChildEventListener = null;

        recyclerView.setAdapter(null);

        mMessagesRecyclerViewAdapter = null;
    }

    private void updatePreferences() {
        mSharedPreferencesHelper.put(Preferences.INTRODUCTION, false);
    }

    private void viewQr() {
        try {
            final String uid = mSharedPreferencesHelper.get(Preferences.UID, null);

            final Bitmap bitmap = mQrCodeHelper.encodeQr(uid);

            final QrCodeDialogFragment qrCodeDialogFragment = QrCodeDialogFragment.newInstance(bitmap);
            qrCodeDialogFragment.show(getSupportFragmentManager(), null);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
