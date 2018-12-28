package application.oneshot.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import application.oneshot.R;
import application.oneshot.models.Contact;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsRecyclerViewAdapter
        extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.ContactRecyclerViewHolder> {

    private ArrayList<Contact> mContacts;
    private ContactsRecyclerViewAdapterBase mRecyclerViewAdapterBase;

    public ContactsRecyclerViewAdapter(Context context, ArrayList<Contact> contacts) {
        this.mContacts = contacts;
        this.mRecyclerViewAdapterBase = ((ContactsRecyclerViewAdapterBase) context);
    }

    public void changeDataSet(ArrayList<Contact> contacts) {
        mContacts = new ArrayList<>(contacts);

        mRecyclerViewAdapterBase.dataSetChanged(contacts);

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(ContactRecyclerViewHolder holder, int position) {
        final Contact contact = mContacts.get(position);

        holder.textViewTitle.setText(contact.getUid());
        holder.textViewSubtitle.setText(contact.getAlias());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerViewAdapterBase.editContact(contact);
            }
        });
    }

    @Override
    public ContactRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_recycler_view_item, parent, false);

        return new ContactRecyclerViewHolder(view);
    }

    public interface ContactsRecyclerViewAdapterBase {
        void dataSetChanged(ArrayList<Contact> contacts);

        void editContact(Contact contact);
    }

    class ContactRecyclerViewHolder
            extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view_title)
        TextView textViewTitle;
        @BindView(R.id.text_view_subtitle)
        TextView textViewSubtitle;

        ContactRecyclerViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
