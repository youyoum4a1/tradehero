package com.tradehero.th.fragments.updatecenter;

import com.tradehero.th.fragments.updatecenter.messages.MessageItemView;
import com.tradehero.th.fragments.updatecenter.messages.MessagesCenterFragment;
import com.tradehero.th.fragments.updatecenter.messages.MessagesView;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationClickHandler;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationItemView;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsView;
import dagger.Component;

@Component
public interface FragmentUpdateCenterComponent
{
    void injectUpdateCenterFragment(UpdateCenterFragment target);
    void injectNotificationsView(NotificationsView target);
    void injectNotificationItemView(NotificationItemView target);

    void injectMessagesCenterFragment(MessagesCenterFragment target);
    void injectNotificationsCenterFragment(NotificationsCenterFragment target);
    void injectUpdateCenterResideMenuItem(UpdateCenterResideMenuItem target);

    void injectMessagesView(MessagesView target);
    void injectMessageItemView(MessageItemView target);
    void injectNotificationClickHandler(NotificationClickHandler target);
}
