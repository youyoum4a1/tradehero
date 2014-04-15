package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wangliang on 3/4/14.
 */
public class MessageListAdapter extends ArrayDTOAdapter<MessageHeaderId, MessageItemViewWrapper> implements
        View.OnClickListener
{

    public static interface MessageOnClickListener
    {
        public static final int TYPE_ICON = 1;
        public static final int TYPE_CONTENT = 2;
        void onMessageClick(int position,int type);

    }

    private MessageOnClickListener messageOnClickListener;
    private Set<Integer> markedDeletedIds;

    public MessageListAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);

    }

    public void initMarkDeletedIds(Collection<Integer> ids)
    {
        if (markedDeletedIds == null)
        {
            markedDeletedIds = new HashSet<>();
        }
        if (ids == null)
        {
            return;
        }
        markedDeletedIds.addAll(ids);
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
            Integer position = (Integer)v.getTag();
            if (v.getId() == R.id.main_content_wrapper)
            {
                messageOnClickListener.onMessageClick(position,MessageOnClickListener.TYPE_CONTENT);
            }
            else if(v.getId() == R.id.message_item_icon)
            {
                messageOnClickListener.onMessageClick(position,MessageOnClickListener.TYPE_ICON);
            }
        }

    }

    @Override protected void fineTune(int position, MessageHeaderId dto, MessageItemViewWrapper dtoView)
    {
        // nothing for now
    }

    @Override public int getCount()
    {
        return super.getCount();
    }

    @Override public MessageHeaderId getItem(int i)
    {
        Object o = super.getItem(i);
        if (o != null)
        {
            MessageHeaderId msgId =  ((MessageHeaderId)o);
            return msgId;
        }
        return null;
    }

    public void appendMore(List<MessageHeaderId> newItems)
    {
        if (newItems == null)
        {
            return;
        }
        newItems = filter(newItems);
        List<MessageHeaderId> itemCopied = items != null ? new ArrayList<>(items) : new ArrayList<MessageHeaderId>();
        for (MessageHeaderId messageHeaderId : newItems)
        {
            if (!checkExist(messageHeaderId.key))
            {
                itemCopied.add(messageHeaderId);
            }
        }
        setItems(itemCopied);
        notifyDataSetChanged();
    }
    //@Override public void setItems(List<MessageHeaderId> newItems)
    //{
    //    List<MessageHeaderId> itemCopied =
    //            (items != null) ? new ArrayList<>(items) : new ArrayList<MessageHeaderId>();
    //    for (MessageHeaderId messageHeaderId : newItems)
    //    {
    //        if (!checkExist(messageHeaderId.key))
    //        {
    //            itemCopied.add(messageHeaderId);
    //        }
    //    }
    //    super.setItems(itemCopied);
    //}

    @Override public void clear()
    {
        super.clear();
        if (items != null)
        {
            items.clear();
            notifyDataSetChanged();
        }
    }

    private void filter()
    {
        List<MessageHeaderId> itemCopied = new ArrayList<MessageHeaderId>();
        for(MessageHeaderId messageHeaderId:items)
        {
            if (!markedDeletedIds.contains(messageHeaderId.key))
            {
                itemCopied.add(messageHeaderId);

            }
        }
        super.setItems(itemCopied);
    }

    private List<MessageHeaderId> filter(List<MessageHeaderId> messageHeaderIds)
    {
        List<MessageHeaderId> itemCopied = new ArrayList<MessageHeaderId>();
        for(MessageHeaderId messageHeaderId:messageHeaderIds)
        {
            if (!markedDeletedIds.contains(messageHeaderId.key))
            {
                itemCopied.add(messageHeaderId);

            }
        }
        return itemCopied;

    }

    private boolean checkExist(int messageId)
    {
        if (items == null)
        {
            return false;
        }
        for (MessageHeaderId id:items)
        {
            if (messageId == id.key)
            {
                return true;
            }
        }
        return false;
    }

    public void markDeleted(int messageId,boolean marlDeleted){
        if (marlDeleted)
        {
            markedDeletedIds.add(messageId);
        }
        else
        {
            markedDeletedIds.remove(messageId);
        }
        filter();
        notifyDataSetChanged();
    }

    public void setMessageOnClickListener(MessageOnClickListener messageOnClickListener)
    {
        this.messageOnClickListener = messageOnClickListener;
    }

}
