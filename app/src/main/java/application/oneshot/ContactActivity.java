package application.oneshot;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import javax.inject.Inject;

import application.oneshot.constants.Extras;
import application.oneshot.dataaccess.ContactDataAccess;
import application.oneshot.fragments.AlertDialogFragment;
import application.oneshot.models.Contact;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class ContactActivity
        extends BaseActivity
        implements AlertDialogFragment.AlertDialogListener {

    @Inject
    ContactDataAccess mContactDataAccess;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edit_text_uid)
    TextView editTextUid;
    @BindView(R.id.edit_text_alias)
    TextView editTextAlias;

    private Contact mContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mContact = (Contact) getIntent().getSerializableExtra(Extras.INTENT_EXTRA);

        if (mContact != null) {
            editTextUid.setText(mContact.getUid());
            editTextAlias.setText(mContact.getAlias());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_contact_options_menu, menu);

        return true;
    }

    @Override
    public void onDialogNegativeClick(int callback) {
    }

    @Override
    public void onDialogPositiveClick(int callback) {
        if (mContact != null) {
            delete();
        }

        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.action_save) {
            if (mContact == null) {
                create();
            } else {
                update();
            }

            NavUtils.navigateUpFromSameTask(this);

            return true;
        } else if (id == R.id.action_delete) {
            showDialog(getString(R.string.alert_dialog_contact));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void create() {
        mContactDataAccess.open();
        mContactDataAccess.create(String.valueOf(editTextUid.getText()), String.valueOf(editTextAlias.getText()));
        mContactDataAccess.close();
    }

    private void delete() {
        mContactDataAccess.open();
        mContactDataAccess.delete(mContact);
        mContactDataAccess.close();
    }

    private void update() {
        mContact.setUid(String.valueOf(editTextUid.getText()));
        mContact.setAlias(String.valueOf(editTextAlias.getText()));

        mContactDataAccess.open();
        mContactDataAccess.update(mContact);
        mContactDataAccess.close();
    }

    private void showDialog(String message) {
        final AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(message);
        alertDialogFragment.show(getFragmentManager(), Extras.INTENT_EXTRA);
    }
}
