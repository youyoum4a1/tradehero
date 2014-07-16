package com.tradehero.th.fragments.updatecenter.notifications;

import android.content.Context;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.notification.NotificationDTO;

public class NotificationListAdapter extends ArrayDTOAdapterNew<NotificationDTO, NotificationItemView>
{
    //<editor-fold desc="Constructors">
    public NotificationListAdapter(Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
    //</editor-fold>
}
