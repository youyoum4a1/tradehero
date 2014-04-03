package com.tradehero.th.fragments.updatecenter;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.notification.NotificationKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thonguyen on 3/4/14.
 */
public class NotificationListAdapter extends ArrayDTOAdapter<NotificationKey, NotificationItemView>
{

    public NotificationListAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override protected void fineTune(int position, NotificationKey dto, NotificationItemView dtoView)
    {
        // nothing for now
    }

    public void appendMore(List<NotificationKey> newItems)
    {
        List<NotificationKey> itemCopied = items != null ? new ArrayList<>(items) : new ArrayList<NotificationKey>();
        itemCopied.addAll(newItems);
        setItems(itemCopied);
        notifyDataSetChanged();
    }
}
