package com.tradehero.th.fragments.updatecenter;

import android.support.v4.app.Fragment;
import com.tradehero.th.R;

/**
 * Created by tradehero on 14-4-3.
 */
public enum MessageType
{

    MESSAGES(R.string.message_center_tab_message, 0, 0,
            MessagesCenterFragment.class),

    Nitifications(R.string.message_center_tab_notification, 0, 0,
            NotificationsCenterFragment.class);

    private MessageType(int titleRes, int pageIndex, int id, Class<? extends Fragment> tabClass)
    {
        this.titleRes = titleRes;
        this.pageIndex = pageIndex;
        this.id = id;
        this.tabClass = tabClass;
    }

    public final int titleRes;
    public final int pageIndex;
    public final int id;
    public final Class<? extends Fragment> tabClass;
}

