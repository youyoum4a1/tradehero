package com.tradehero.th.fragments.updatecenter.notifications;

import android.content.Context;
import android.os.Bundle;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.PrivateMessageKey;
import com.tradehero.th.api.discussion.key.SecurityDiscussionKey;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationTradeDTO;
import com.tradehero.th.api.notification.NotificationType;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.discussion.NewsDiscussionFragment;
import com.tradehero.th.fragments.discussion.TimelineDiscussionFragment;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionCommentFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.social.message.ReplyPrivateMessageFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import timber.log.Timber;

/**
 * Created by tho on 4/17/2014.
 */
public class NotificationClickHandler
{
    private final NotificationDTO notificationDTO;
    private final Navigator navigator;
    private final Context context;

    public NotificationClickHandler(Context context, NotificationDTO notificationDTO)
    {
        this.context = context;
        this.notificationDTO = notificationDTO;

        if (context instanceof NavigatorActivity)
        {
            this.navigator = ((NavigatorActivity) context).getNavigator();
        }
        else
        {
            throw new IllegalArgumentException("Context needed to be NavigatorActivity");
        }
    }

    /**
     * Handle click event on NotificationItemView
     * @return true if handled, false otherwise
     */
    public boolean handleNotificationItemClicked()
    {
        Timber.d("Handling notification (%d)", notificationDTO.pushId);
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

        return false;
    }

    private void handleContributorsNotification()
    {
        Integer replyTypeId = notificationDTO.replyableTypeId;
        if (replyTypeId != null)
        {
            DiscussionType discussionType = DiscussionType.fromValue(replyTypeId);

            switch (discussionType)
            {
                case NEWS:
                {
                    NewsItemDTOKey newsItemDTOKey = new NewsItemDTOKey(notificationDTO.replyableId);

                    Bundle bundle = new Bundle();
                    bundle.putBundle(NewsDiscussionFragment.DISCUSSION_KEY_BUNDLE_KEY, newsItemDTOKey.getArgs());
                    navigator.pushFragment(NewsDiscussionFragment.class, bundle);
                }
                break;

                case SECURITY:
                {
                    SecurityDiscussionKey securityDiscussionKey = new SecurityDiscussionKey(notificationDTO.replyableId);

                    Bundle bundle = new Bundle();
                    bundle.putBundle(SecurityDiscussionCommentFragment.DISCUSSION_KEY_BUNDLE_KEY, securityDiscussionKey.getArgs());
                    navigator.pushFragment(SecurityDiscussionCommentFragment.class, bundle);
                }
                break;

                case PRIVATE_MESSAGE:
                {
                    Bundle args = new Bundle();
                    ReplyPrivateMessageFragment.putCorrespondentUserBaseKey(args, new UserBaseKey(notificationDTO.referencedUserId));
                    ReplyPrivateMessageFragment.putDiscussionKey(args, new PrivateMessageKey(notificationDTO.replyableId));
                    navigator.pushFragment(ReplyPrivateMessageFragment.class, args);
                }
                break;

                case TIMELINE_ITEM:
                {
                    TimelineItemDTOKey timelineItemDTOKey = new TimelineItemDTOKey(notificationDTO.replyableId);

                    Bundle bundle = new Bundle();
                    bundle.putBundle(TimelineDiscussionFragment.DISCUSSION_KEY_BUNDLE_KEY, timelineItemDTOKey.getArgs());
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
            bundle.putInt(TimelineFragment.BUNDLE_KEY_SHOW_USER_ID, notificationDTO.referencedUserId);
            navigator.pushFragment(PushableTimelineFragment.class, bundle);
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
                    args.putBundle(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE, ownedPortfolioId.getArgs());
                    navigator.pushFragment(PositionListFragment.class, args);
                }
            }
        }
    }
}
