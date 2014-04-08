package com.tradehero.th.fragments.updatecenter;

import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.fragments.updatecenter.messages.MessagesCenterFragment;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsCenterFragment;

/**
 * Created by tradehero on 14-4-3.
 */
public enum UpdateCenterTabType
{
    Messages(R.string.message_center_tab_message, 0, 0, MessagesCenterFragment.class),
    Notifications(R.string.message_center_tab_notification, 0, 0, NotificationsCenterFragment.class);

    public final int id;
    public final int titleRes;
    public final int pageIndex;
    public final Class<? extends Fragment> tabClass;

    private UpdateCenterTabType(int titleRes, int pageIndex, int id, Class<? extends Fragment> tabClass)
    {
        this.id = id;
        this.titleRes = titleRes;
        this.pageIndex = pageIndex;
        this.tabClass = tabClass;
    }
}

