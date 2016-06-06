package com.androidth.general.fragments.updatecenter;

import com.androidth.general.fragments.updatecenter.messageNew.MessagesCenterNewFragment;
import com.androidth.general.fragments.updatecenter.notifications.NotificationClickHandler;
import com.androidth.general.fragments.updatecenter.notifications.NotificationItemView;
import com.androidth.general.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.androidth.general.fragments.updatecenter.notifications.NotificationsView;
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
