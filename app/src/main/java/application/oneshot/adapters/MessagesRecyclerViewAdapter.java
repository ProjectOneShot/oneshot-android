package application.oneshot.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import application.oneshot.R;
import application.oneshot.beans.Conversation;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagesRecyclerViewAdapter
        extends RecyclerView.Adapter<MessagesRecyclerViewAdapter.MessageRecyclerViewHolder> {

    private ArrayList<Conversation> mConversations;
    private MessagesRecyclerViewAdapterBase mRecyclerViewAdapterBase;

    public MessagesRecyclerViewAdapter(Context context) {
        mConversations = new ArrayList<>();
        mRecyclerViewAdapterBase = ((MessagesRecyclerViewAdapterBase) context);
    }

    public void addItem(Conversation conversation) {
        mConversations.add(conversation);

        notifyDataSetChanged();
    }

    public Conversation getItem(int index) {
        return mConversations.get(index);
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(final MessageRecyclerViewHolder holder, int position) {
        final Conversation conversation = mConversations.get(position);

        if (conversation.getContact()
                .getAlias() == null) {
            holder.textViewSender.setText(conversation.getContact()
                    .getUid());
        } else {
            holder.textViewSender.setText(conversation.getContact()
                    .getAlias());
        }

        holder.textViewSubject.setText(conversation.getMessage()
                .getSubject());
        holder.textViewContent.setText(conversation.getMessage()
                .getContent());

        if (conversation.getMessage()
                .isRead()) {
            holder.imageViewIconUnread.setVisibility(View.GONE);

            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams) holder.textViewSender.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.END_OF);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);

            holder.textViewSender.setLayoutParams(layoutParams);
        } else {
            holder.imageViewIconUnread.setVisibility(View.VISIBLE);
        }

        final CharSequence relativeTimeSpan = DateUtils.getRelativeTimeSpanString(conversation.getMessage()
                .getCreated(), System.currentTimeMillis(), 0, DateUtils.FORMAT_ABBREV_ALL);

        holder.textViewTimeSpan.setText(relativeTimeSpan);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerViewAdapterBase.onItemClick(conversation);
            }
        });
    }

    @Override
    public MessageRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(application.oneshot.R.layout.message_recycler_view_item, parent, false);

        return new MessageRecyclerViewHolder(view);
    }

    public void removeItem(int position) {
        mConversations.remove(position);

        notifyItemRemoved(position);
    }

    public interface MessagesRecyclerViewAdapterBase {
        void onItemClick(Conversation conversation);
    }

    public class MessageRecyclerViewHolder
            extends RecyclerView.ViewHolder {

        @BindView(R.id.relative_layout_foreground)
        public RelativeLayout relativeLayoutForeground;

        @BindView(R.id.text_view_sender)
        TextView textViewSender;
        @BindView(R.id.text_view_subject)
        TextView textViewSubject;
        @BindView(R.id.text_view_content)
        TextView textViewContent;
        @BindView(R.id.image_view_icon_unread)
        ImageView imageViewIconUnread;
        @BindView(R.id.text_view_time_span)
        TextView textViewTimeSpan;

        MessageRecyclerViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
