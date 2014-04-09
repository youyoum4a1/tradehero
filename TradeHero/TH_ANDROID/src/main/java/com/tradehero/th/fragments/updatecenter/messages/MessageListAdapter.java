package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.discussion.key.MessageId;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangliang on 3/4/14.
 */
public class MessageListAdapter extends ArrayDTOAdapter<MessageId, MessageItemView>
{
    public MessageListAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override protected void fineTune(int position, MessageId dto, MessageItemView dtoView)
    {
        // nothing for now
    }

    public void appendMore(List<MessageId> newItems)
    {
        List<MessageId> itemCopied = items != null ? new ArrayList<>(items) : new ArrayList<MessageId>();
        itemCopied.addAll(newItems);
        setItems(itemCopied);
        notifyDataSetChanged();
    }
}
