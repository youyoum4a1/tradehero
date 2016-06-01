package com.ayondo.academy.fragments.updatecenter;

import com.ayondo.academy.fragments.updatecenter.messageNew.MessagesCenterNewFragment;
import com.ayondo.academy.fragments.updatecenter.notifications.NotificationClickHandler;
import com.ayondo.academy.fragments.updatecenter.notifications.NotificationItemView;
import com.ayondo.academy.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.ayondo.academy.fragments.updatecenter.notifications.NotificationsView;
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
