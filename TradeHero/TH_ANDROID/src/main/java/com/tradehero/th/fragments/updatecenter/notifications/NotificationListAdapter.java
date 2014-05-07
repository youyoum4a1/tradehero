package com.tradehero.th.fragments.updatecenter.notifications;

import android.content.Context;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.notification.NotificationKey;

public class NotificationListAdapter extends ArrayDTOAdapterNew<NotificationKey, NotificationItemView>
{
    //<editor-fold desc="Constructors">
    public NotificationListAdapter(Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
    //</editor-fold>
}
