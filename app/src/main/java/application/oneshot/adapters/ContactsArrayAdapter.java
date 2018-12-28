package application.oneshot.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import application.oneshot.R;
import application.oneshot.models.Contact;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsArrayAdapter
        extends ArrayAdapter<Contact> {

    private List<Contact> mContacts;
    private List<Contact> mContactsResult;

    public ContactsArrayAdapter(Context context, List<Contact> contacts) {
        super(context, 0, contacts);

        this.mContacts = contacts;
        this.mContactsResult = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mContactsResult.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new ContactsFilter(this, mContacts);
    }

    @Override
    public Contact getItem(int index) {
        return mContactsResult.get(index);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final Contact contact = mContactsResult.get(position);

        final ViewHolder viewHolder;

        if (convertView == null) {
            final LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.contact_array_adapter_item, parent, false);

            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textViewTitle.setText(contact.getUid());
        viewHolder.textViewSubtitle.setText(contact.getAlias());

        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.text_view_title)
        TextView textViewTitle;
        @BindView(R.id.text_view_subtitle)
        TextView textViewSubtitle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class ContactsFilter
            extends Filter {

        private ContactsArrayAdapter mContactsArrayAdapter;
        private List<Contact> mFilteredContacts;
        private List<Contact> mFilteredContactsResult;

        private ContactsFilter(ContactsArrayAdapter contactsArrayAdapter, List<Contact> contacts) {
            super();

            this.mContactsArrayAdapter = contactsArrayAdapter;
            this.mFilteredContacts = contacts;
            this.mFilteredContactsResult = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            mFilteredContactsResult.clear();

            final FilterResults filterResults = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                mFilteredContactsResult.addAll(mFilteredContacts);
            } else {
                final String filterPattern = constraint.toString()
                        .toLowerCase()
                        .trim();

                for (final Contact contact : mFilteredContacts) {
                    final String contactConstraint = contact.getUid() + contact.getAlias();

                    if (contactConstraint.toLowerCase()
                            .contains(filterPattern)) {
                        mFilteredContactsResult.add(contact);
                    }
                }
            }

            filterResults.values = mFilteredContactsResult;
            filterResults.count = mFilteredContactsResult.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mContactsArrayAdapter.mContactsResult.clear();
            mContactsArrayAdapter.mContactsResult.addAll((List) results.values);
            mContactsArrayAdapter.notifyDataSetChanged();
        }
    }
}
