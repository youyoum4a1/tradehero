package com.ayondo.academy.fragments.updatecenter.notifications;

import android.content.Context;
import android.os.Bundle;
import com.ayondo.academy.activities.PrivateDiscussionActivity;
import com.ayondo.academy.api.discussion.DiscussionType;
import com.ayondo.academy.api.discussion.key.DiscussionKeyFactory;
import com.ayondo.academy.api.discussion.key.SecurityDiscussionKey;
import com.ayondo.academy.api.news.key.NewsItemDTOKey;
import com.ayondo.academy.api.notification.NotificationDTO;
import com.ayondo.academy.api.notification.NotificationTradeDTO;
import com.ayondo.academy.api.notification.NotificationType;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.timeline.key.TimelineItemDTOKey;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.fragments.discussion.NewsDiscussionFragment;
import com.ayondo.academy.fragments.discussion.TimelineDiscussionFragment;
import com.ayondo.academy.fragments.discussion.stock.SecurityDiscussionCommentFragment;
import com.ayondo.academy.fragments.position.PositionListFragment;
import com.ayondo.academy.fragments.timeline.MeTimelineFragment;
import com.ayondo.academy.fragments.timeline.PushableTimelineFragment;
import com.ayondo.academy.inject.HierarchyInjector;
import javax.inject.Inject;
import timber.log.Timber;

public class NotificationClickHandler
{
    private final NotificationDTO notificationDTO;

    @Inject CurrentUserId currentUserId;
    @Inject DashboardNavigator navigator;

    public NotificationClickHandler(
            Context context,
            NotificationDTO notificationDTO)
    {
        this.notificationDTO = notificationDTO;

        HierarchyInjector.inject(context, this);
    }

    /**
     * Handle click event on NotificationItemView
     * @return true if handled, false otherwise
     */
    public boolean handleNotificationItemClicked()
    {
        Timber.d("Handling notification (%d)", notificationDTO.pushId);
        if (notificationDTO.pushTypeId != null)
        {
            NotificationType notificationType = NotificationType.fromType(notificationDTO.pushTypeId);

            switch (notificationType)
            {
                case HeroAction:
                    handleHeroActionNotification();
                    return true;
                case LowBalance:
                case SubscriptionExpired:
                    handleLowCreditNotificationWithDisplayAlert();
                    return true;

                case ReferralSucceeded:
                    handleReferralSucceededNotificationWithAlert();
                    return true;

                case FriendStartedFollowing:
                case PositionClosed:
                case TradeOfTheWeek:
                    handleFriendStartedFollowingNotification();
                    return true;

                case TradeInCompetition:
                case CompetitionInvite:
                    handleTradeInCompetitionOrCompetitionInvite();
                    return true;

                case StockAlert:
                    handleStockAlertNotification();
                    return true;

                case GeneralAnnouncement:
                case FreeCash:
                    handleGenericNotificationWithAlert();
                    return true;

                case ResetPortfolio:
                    handleResetPortfolioNotification();
                    return true;

                case PrivateMessage:
                case BroadcastMessage:
                case NotifyOriginator:
                case NotifyContributors:
                    handleContributorsNotification();
                    return true;
            }
        }

        return false;
    }

    private void handleContributorsNotification()
    {
        Integer replyTypeId = notificationDTO.replyableTypeId;
        if (replyTypeId != null)
        {
            DiscussionType discussionType = DiscussionType.fromValue(replyTypeId);
            Bundle bundle = new Bundle();

            switch (discussionType)
            {
                case NEWS:
                {
                    NewsItemDTOKey newsItemDTOKey = new NewsItemDTOKey(notificationDTO.replyableId);
                    NewsDiscussionFragment.putDiscussionKey(bundle, newsItemDTOKey);
                    navigator.pushFragment(NewsDiscussionFragment.class, bundle);
                }
                break;

                case SECURITY:
                {
                    SecurityDiscussionKey securityDiscussionKey = new SecurityDiscussionKey(notificationDTO.replyableId);
                    SecurityDiscussionCommentFragment.putDiscussionKey(bundle, securityDiscussionKey);
                    navigator.pushFragment(SecurityDiscussionCommentFragment.class, bundle);
                }
                break;

                case PRIVATE_MESSAGE:
                {
                    // Both are needed in ReplyPrivateMessageFragment
                    if (notificationDTO.referencedUserId == null && notificationDTO.threadId == null)
                    {
                        // server side problem? report it
                        Timber.e("Notification for Private messaging (id=%d) but does not contain neither referencedUserId nor threadId",
                                notificationDTO.pushId);
                        return;
                    }
                    if (notificationDTO.referencedUserId != null)
                    {
                        PrivateDiscussionActivity.putCorrespondentUserBaseKey(bundle, new UserBaseKey(notificationDTO.referencedUserId));
                    }
                    if (notificationDTO.threadId != null)
                    {
                        PrivateDiscussionActivity.putDiscussionKey(bundle, DiscussionKeyFactory.create(discussionType, notificationDTO.threadId));
                    }
                    navigator.launchActivity(PrivateDiscussionActivity.class, bundle);
                }
                break;

                case BROADCAST_MESSAGE:
                {
                    // Both are needed in ReplyPrivateMessageFragment
                    if (notificationDTO.referencedUserId == null && notificationDTO.replyableId == null)
                    {
                        // server side problem? report it
                        Timber.e("Notification for Private messaging (id=%d) but does not contain neither referencedUserId nor replyableId",
                                notificationDTO.pushId);
                        return;
                    }
                    if (notificationDTO.referencedUserId != null)
                    {
                        PrivateDiscussionActivity.putCorrespondentUserBaseKey(bundle, new UserBaseKey(notificationDTO.referencedUserId));
                    }
                    if (notificationDTO.replyableId != null)
                    {
                        PrivateDiscussionActivity.putDiscussionKey(bundle, DiscussionKeyFactory.create(discussionType, notificationDTO.replyableId));
                    }
                    navigator.launchActivity(PrivateDiscussionActivity.class, bundle);
                }
                break;

                case TIMELINE_ITEM:
                {
                    TimelineItemDTOKey timelineItemDTOKey = new TimelineItemDTOKey(notificationDTO.replyableId);
                    TimelineDiscussionFragment.putDiscussionKey(bundle, timelineItemDTOKey);
                    navigator.pushFragment(TimelineDiscussionFragment.class, bundle);
                }
                break;
            }
        }
    }

    private void handleResetPortfolioNotification()
    {

    }

    private void handleGenericNotificationWithAlert()
    {

    }

    private void handleStockAlertNotification()
    {

    }

    private void handleTradeInCompetitionOrCompetitionInvite()
    {

    }

    private void handleFriendStartedFollowingNotification()
    {
        if (notificationDTO != null && notificationDTO.referencedUserId != null)
        {
            Bundle bundle = new Bundle();
            UserBaseKey referencedUser = new UserBaseKey(notificationDTO.referencedUserId);
            if (currentUserId.toUserBaseKey().equals(referencedUser))
            {
                navigator.pushFragment(MeTimelineFragment.class, bundle);
            }
            else
            {
                PushableTimelineFragment.putUserBaseKey(bundle, referencedUser);
                navigator.pushFragment(PushableTimelineFragment.class, bundle);
            }
        }
    }

    private void handleReferralSucceededNotificationWithAlert()
    {
    }

    private void handleLowCreditNotificationWithDisplayAlert()
    {
        if (notificationDTO.stockAlert != null)
        {
            // TODO
        }
        else
        {
            // TODO
        }
    }

    /**
     * TODO transaction history screen need to be implemented
     */
    private void handleHeroActionNotification()
    {
        Integer userId = notificationDTO.referencedUserId;
        if (userId != null)
        {
            NotificationTradeDTO notificationTradeDTO = notificationDTO.trade;

            if (notificationTradeDTO != null)
            {
                Integer portfolioId = notificationTradeDTO.portfolioId;

                if (portfolioId != null)
                {
                    Bundle args = new Bundle();
                    OwnedPortfolioId ownedPortfolioId = new OwnedPortfolioId(userId, portfolioId);
                    PositionListFragment.putGetPositionsDTOKey(args, ownedPortfolioId);
                    PositionListFragment.putShownUser(args, ownedPortfolioId.getUserBaseKey());
                    navigator.pushFragment(PositionListFragment.class, args);
                }
            }
        }
    }
}
