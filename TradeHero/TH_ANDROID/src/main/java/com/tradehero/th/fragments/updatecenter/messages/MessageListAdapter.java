package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageListAdapter extends ArrayDTOAdapter<MessageHeaderId, MessageItemViewWrapper>
        implements
        View.OnClickListener
{
    private MessageOnClickListener messageOnClickListener;
    private Set<MessageHeaderId> markedDeletedIds;

    public MessageListAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
        markedDeletedIds = new HashSet<>();
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        View v = super.getView(position, convertView, viewGroup);
        View contentView = v.findViewById(R.id.main_content_wrapper);
        View iconView = v.findViewById(R.id.message_item_icon);
        contentView.setTag(position);
        iconView.setTag(position);
        iconView.setOnClickListener(this);
        contentView.setOnClickListener(this);
        //((SwipeListView)viewGroup).recycle(v, position);
        return v;
    }

    @Override public void onClick(View v)
    {
        if (v.getTag() != null && messageOnClickListener != null)
        {
            Integer position = (Integer) v.getTag();
            if (v.getId() == R.id.main_content_wrapper)
            {
                messageOnClickListener.onMessageClick(position,
                        MessageOnClickListener.TYPE_CONTENT);
            }
            else if (v.getId() == R.id.message_item_icon)
            {
                messageOnClickListener.onMessageClick(position, MessageOnClickListener.TYPE_ICON);
            }
        }
    }

    @Override protected void fineTune(int position, MessageHeaderId dto,
            MessageItemViewWrapper dtoView)
    {
        // nothing for now
    }

    @Override public MessageHeaderId getItem(int i)
    {
        return (MessageHeaderId) super.getItem(i);
    }

    public void appendMore(List<MessageHeaderId> newItems)
    {
        if (newItems == null)
        {
            return;
        }
        newItems = filter(newItems);
        List<MessageHeaderId> itemCopied =
                items != null ? new ArrayList<>(items) : new ArrayList<MessageHeaderId>();
        for (MessageHeaderId messageHeaderId : newItems)
        {
            if (!isInItems(messageHeaderId))
            {
                itemCopied.add(messageHeaderId);
            }
        }
        setItems(itemCopied);
        notifyDataSetChanged();
    }

    private void filterAndSet()
    {
        super.setItems(filter(items));
    }

    private List<MessageHeaderId> filter(List<MessageHeaderId> messageHeaderIds)
    {
        List<MessageHeaderId> itemCopied = new ArrayList<>();
        for (MessageHeaderId messageHeaderId : messageHeaderIds)
        {
            if (!markedDeletedIds.contains(messageHeaderId))
            {
                itemCopied.add(messageHeaderId);
            }
        }
        return itemCopied;
    }

    private boolean isInItems(MessageHeaderId messageId)
    {
        return items != null && items.contains(messageId);
    }

    public void markDeleted(MessageHeaderId messageId, boolean markDeleted)
    {
        if (markDeleted)
        {
            markedDeletedIds.add(messageId);
        }
        else
        {
            markedDeletedIds.remove(messageId);
        }
        filterAndSet();
        notifyDataSetChanged();
    }

    public void setMessageOnClickListener(MessageOnClickListener messageOnClickListener)
    {
        this.messageOnClickListener = messageOnClickListener;
    }

    public static interface MessageOnClickListener
    {
        public static final int TYPE_ICON = 1;
        public static final int TYPE_CONTENT = 2;

        void onMessageClick(int position, int type);
    }
}
