package com.tradehero.th.fragments.updatecenter;

import com.tradehero.th.fragments.updatecenter.messageNew.MessagesCenterNewFragment;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationClickHandler;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationItemView;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsView;
import dagger.Module;

@Module(
        injects = {
                UpdateCenterFragment.class,
                NotificationsView.class,
                NotificationItemView.class,

                MessagesCenterNewFragment.class,
                NotificationsCenterFragment.class,

                NotificationClickHandler.class,
        },
        library = true,
        complete = false
)
public class FragmentUpdateCenterModule
{
}
