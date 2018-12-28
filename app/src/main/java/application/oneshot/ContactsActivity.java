package application.oneshot;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

import javax.inject.Inject;

import application.oneshot.adapters.ContactsRecyclerViewAdapter;
import application.oneshot.constants.Extras;
import application.oneshot.dataaccess.ContactDataAccess;
import application.oneshot.models.Contact;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;

public class ContactsActivity
        extends BaseActivity
        implements ContactsRecyclerViewAdapter.ContactsRecyclerViewAdapterBase {

    @Inject
    ContactDataAccess mContactDataAccess;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.text_view)
    TextView textView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private ArrayList<Contact> mContacts;

    private ContactsRecyclerViewAdapter mContactsRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contacts);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mContacts = new ArrayList<>();

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());

        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void onResume() {
        super.onResume();

        (new BindRecyclerViewAdapter()).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_contacts_options_menu, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.search_view)
                .getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String string) {
                final ArrayList<Contact> contacts = filterRecyclerView(mContacts, string);

                mContactsRecyclerViewAdapter.changeDataSet(contacts);

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String string) {
                return false;
            }
        });

        return true;
    }

    @OnClick(R.id.fab)
    public void onClick(View view) {
        showCreateRecipientDialog();
    }

    @Override
    public void dataSetChanged(ArrayList<Contact> contacts) {
        toggleNoRecipientsMessage(contacts);
    }

    @Override
    public void editContact(final Contact contact) {
        showEditRecipientDialog(contact);
    }

    private ArrayList<Contact> filterRecyclerView(ArrayList<Contact> contacts, String string) {
        final ArrayList<Contact> result = new ArrayList<>();

        for (final Contact contact : contacts) {
            // Filter on UID and alias.
            final String text = contact.getUid()
                    .toLowerCase() + contact.getAlias()
                    .toLowerCase();

            if (text.contains(string.toLowerCase())) {
                result.add(contact);
            }
        }

        return result;
    }

    private void showCreateRecipientDialog() {
        final Intent intent = new Intent(this, ContactActivity.class);

        startActivity(intent);
    }

    private void showEditRecipientDialog(final Contact contact) {
        final Intent intent = new Intent(this, ContactActivity.class);
        intent.putExtra(Extras.INTENT_EXTRA, contact);

        startActivity(intent);
    }

    private void toggleNoRecipientsMessage(ArrayList<Contact> contacts) {
        // TODO: Show image?
        if (contacts.size() > 0) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    private class BindRecyclerViewAdapter
            extends AsyncTask<String, Void, ArrayList<Contact>> {

        @Override
        protected ArrayList<Contact> doInBackground(String... strings) {
            mContactDataAccess.open();

            return mContactDataAccess.getAll();
        }

        @Override
        protected void onPostExecute(ArrayList<Contact> contacts) {
            super.onPostExecute(contacts);

            mContacts = new ArrayList<>(contacts);

            mContactsRecyclerViewAdapter = new ContactsRecyclerViewAdapter(ContactsActivity.this, contacts);

            recyclerView.setAdapter(mContactsRecyclerViewAdapter);

            toggleNoRecipientsMessage(contacts);

            mContactDataAccess.close();
        }
    }
}
