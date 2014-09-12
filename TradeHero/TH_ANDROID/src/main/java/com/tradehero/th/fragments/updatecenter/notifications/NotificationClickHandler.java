package com.tradehero.th.fragments.updatecenter.notifications;

import android.content.Context;
import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.discussion.key.SecurityDiscussionKey;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationTradeDTO;
import com.tradehero.th.api.notification.NotificationType;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.discussion.NewsDiscussionFragment;
import com.tradehero.th.fragments.discussion.TimelineDiscussionFragment;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionCommentFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.social.message.ReplyPrivateMessageFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;
import timber.log.Timber;

public class NotificationClickHandler
{
    private final NotificationDTO notificationDTO;
    private final Context context;

    @Inject DiscussionKeyFactory discussionKeyFactory;
    @Inject THRouter thRouter;
    @Inject CurrentUserId currentUserId;
    @Inject DashboardNavigator navigator;

    public NotificationClickHandler(
            Context context,
            NotificationDTO notificationDTO)
    {
        this.context = context;
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
            Timber.e(new Exception("Just reporting"), "notification %s", notificationDTO);
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
                        ReplyPrivateMessageFragment.putCorrespondentUserBaseKey(bundle, new UserBaseKey(notificationDTO.referencedUserId));
                    }
                    if (notificationDTO.threadId != null)
                    {
                        ReplyPrivateMessageFragment.putDiscussionKey(bundle, discussionKeyFactory.create(discussionType, notificationDTO.threadId));
                    }
                    navigator.pushFragment(ReplyPrivateMessageFragment.class, bundle);
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
                        ReplyPrivateMessageFragment.putCorrespondentUserBaseKey(bundle, new UserBaseKey(notificationDTO.referencedUserId));
                    }
                    if (notificationDTO.replyableId != null)
                    {
                        ReplyPrivateMessageFragment.putDiscussionKey(bundle, discussionKeyFactory.create(discussionType, notificationDTO.replyableId));
                    }
                    navigator.pushFragment(ReplyPrivateMessageFragment.class, bundle);
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
            thRouter.save(bundle, referencedUser);
            if (currentUserId.toUserBaseKey().equals(referencedUser))
            {
                navigator.pushFragment(MeTimelineFragment.class, bundle);
            }
            else
            {
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
