package com.tradehero.th.fragments.updatecenter.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.AppendableArrayDTOAdapter;
import com.tradehero.th.api.notification.NotificationKey;

public class NotificationListAdapter extends AppendableArrayDTOAdapter<NotificationKey, NotificationItemView>
{

    public NotificationListAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override protected void fineTune(int position, NotificationKey dto, NotificationItemView dtoView)
    {
        // nothing for now
    }
}
