package com.tradehero.th.ui;

import com.tradehero.th.fragments.discussion.*;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionCommentFragment;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionFragment;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionItemViewLinear;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionView;
import com.tradehero.th.fragments.social.FollowDialogView;
import com.tradehero.th.fragments.social.friend.SocialFriendUserView;
import com.tradehero.th.fragments.social.message.PrivatePostCommentView;
import com.tradehero.th.fragments.timeline.TimelineItemViewLinear;
import com.tradehero.th.fragments.updatecenter.UpdateCenterFragment;
import com.tradehero.th.fragments.updatecenter.UpdateCenterResideMenuItem;
import com.tradehero.th.fragments.updatecenter.messages.MessageItemView;
import com.tradehero.th.fragments.updatecenter.messages.MessagesCenterFragment;
import com.tradehero.th.fragments.updatecenter.messages.MessagesView;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationClickHandler;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationItemView;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsCenterFragment;
import com.tradehero.th.fragments.updatecenter.notifications.NotificationsView;
import dagger.Module;
import dagger.Provides;
import org.ocpsoft.prettytime.PrettyTime;

import javax.inject.Singleton;

@Module(
        includes = {
                UIComponents.class
        },
        injects = {
                TimelineItemViewLinear.class,
                UpdateCenterFragment.class,
                NotificationsView.class,
                NotificationItemView.class,

                MessagesCenterFragment.class,
                NotificationsCenterFragment.class,
                UpdateCenterResideMenuItem.class,

                MessagesView.class,
                MessageItemView.class,

                SecurityDiscussionView.class,
                SecurityDiscussionFragment.class,
                SecurityDiscussionItemViewLinear.class,
                SecurityDiscussionCommentFragment.class,

                DiscussionView.class,
                PostCommentView.class,
                PrivatePostCommentView.class,

                NewsDiscussionView.class,

                DiscussionEditPostFragment.class,
                SecurityDiscussionEditPostFragment.class,
                DiscussionPostActionButtonsView.class,
                TransactionEditCommentFragment.class,
                MentionActionButtonsView.class,

                FollowDialogView.class,

                NotificationClickHandler.class,
                SocialFriendUserView.class
        },
        complete = false,
        library = true
)
public class UIModule
{
    @Provides PrettyTime providePrettyTime()
    {
        return new PrettyTime();
    }

    @Provides @Singleton AppContainer provideAppContainer()
    {
        return AppContainer.DEFAULT;
    }

    @Provides @Singleton ViewWrapper provideViewWrapper()
    {
        return ViewWrapper.DEFAULT;
    }
}
