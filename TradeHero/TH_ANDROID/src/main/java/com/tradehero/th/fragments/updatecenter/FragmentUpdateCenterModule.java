package com.tradehero.th.fragments.updatecenter;

import com.tradehero.th.fragments.updatecenter.messages.MessageItemView;
import com.tradehero.th.fragments.updatecenter.messages.MessagesCenterFragment;
import com.tradehero.th.fragments.updatecenter.messages.MessagesView;
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

                MessagesCenterFragment.class,
                NotificationsCenterFragment.class,
                UpdateCenterResideMenuItem.class,

                MessagesView.class,
                MessageItemView.class,
                NotificationClickHandler.class,
        },
        library = true,
        complete = false
)
public class FragmentUpdateCenterModule
{
}
