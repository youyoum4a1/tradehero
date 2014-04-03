package com.tradehero.th.fragments.updatecenter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.notification.NotificationKey;

/**
 * Created by thonguyen on 3/4/14.
 */
public class NotificationItemView
        extends RelativeLayout
        implements DTOView<NotificationKey>
{
    private NotificationKey notificationKey;

    //<editor-fold desc="Constructors">
    public NotificationItemView(Context context)
    {
        super(context);
    }

    public NotificationItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NotificationItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void display(NotificationKey notificationKey)
    {
        this.notificationKey = notificationKey;
    }
}
