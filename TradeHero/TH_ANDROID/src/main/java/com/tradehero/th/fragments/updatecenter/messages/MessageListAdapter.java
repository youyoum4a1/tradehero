package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.messages.MessageKey;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationItemView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangliang on 3/4/14.
 */
public class MessageListAdapter extends ArrayDTOAdapter<MessageKey, MessageItemView>
{

    public MessageListAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override protected void fineTune(int position, MessageKey dto, MessageItemView dtoView)
    {
        // nothing for now
    }

    public void appendMore(List<MessageKey> newItems)
    {
        List<MessageKey> itemCopied = items != null ? new ArrayList<>(items) : new ArrayList<MessageKey>();
        itemCopied.addAll(newItems);
        setItems(itemCopied);
        notifyDataSetChanged();
    }
}
