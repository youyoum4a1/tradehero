package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fortysevendeg.android.swipelistview.SwipeListView;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangliang on 3/4/14.
 */
public class MessageListAdapter extends ArrayDTOAdapter<MessageHeaderId, MessageItemViewWrapper>
{
    public MessageListAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        View v = super.getView(position, convertView, viewGroup);
        //((SwipeListView)viewGroup).recycle(v, position);
        return v;
    }

    @Override protected void fineTune(int position, MessageHeaderId dto, MessageItemViewWrapper dtoView)
    {
        // nothing for now
    }

    public void appendMore(List<MessageHeaderId> newItems)
    {
        List<MessageHeaderId> itemCopied = items != null ? new ArrayList<>(items) : new ArrayList<MessageHeaderId>();
        itemCopied.addAll(newItems);
        setItems(itemCopied);
        notifyDataSetChanged();
    }

    @Override public void setItems(List<MessageHeaderId> newItems)
    {
        List<MessageHeaderId> itemCopied = new ArrayList<>(newItems.size());
        for(MessageHeaderId messageHeaderId:newItems)
        {
            if (!messageHeaderId.markDeleted)
            {
                itemCopied.add(messageHeaderId);
            }
        }
        super.setItems(itemCopied);
    }

    public void markDeleted(int position){
        MessageHeaderId messageHeaderId =  getItem(position);
        messageHeaderId.markDeleted = true;
        setItems(items);
        notifyDataSetChanged();
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
}
