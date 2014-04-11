package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.view.LayoutInflater;
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
}
