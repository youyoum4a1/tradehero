package com.tradehero.th.fragments.updatecenter.notifications;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.notification.NotificationTradeDTO;
import com.tradehero.th.api.notification.NotificationType;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created by thonguyen on 3/4/14.
 */
public class NotificationItemView
        extends LinearLayout
        implements DTOView<NotificationKey>
{
    @InjectView(R.id.discussion_content) TextView notificationContent;
    @InjectView(R.id.notification_user_picture) ImageView notificationPicture;
    @InjectView(R.id.discussion_time) TextView notificationTime;

    @Inject NotificationCache notificationCache;
    @Inject PrettyTime prettyTime;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;

    private NotificationKey notificationKey;
    private NotificationDTO notificationDTO;

    private DTOCache.Listener<NotificationKey, NotificationDTO> notificationFetchListener;
    private DTOCache.GetOrFetchTask<NotificationKey, NotificationDTO> notificationFetchTask;

    //<editor-fold desc="Constructors">
    public NotificationItemView(Context context)
    {
        super(context);
    }

    public NotificationItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NotificationItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        DaggerUtils.inject(this);

        notificationFetchListener = new NotificationFetchListener();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        notificationFetchListener = new NotificationFetchListener();
    }

    @Override protected void onDetachedFromWindow()
    {
        detachNotificationFetchTask();

        resetView();

        notificationFetchListener = null;
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @OnClick(R.id.notification_user_picture) void onUserProfileClicked()
    {
        Bundle bundle = new Bundle();
        if (notificationDTO != null && notificationDTO.referencedUserId != null)
        {
            bundle.putInt(TimelineFragment.BUNDLE_KEY_SHOW_USER_ID, notificationDTO.referencedUserId);
            getNavigator().pushFragment(PushableTimelineFragment.class, bundle);
        }
    }

    @Override public void display(NotificationKey notificationKey)
    {
        this.notificationKey = notificationKey;

        fetchNotification();
    }

    private void display(NotificationDTO notificationDTO)
    {
        if (notificationDTO != null)
        {
            notificationContent.setText(notificationDTO.text);
            notificationTime.setText(prettyTime.format(notificationDTO.createdAtUtc));

            if (notificationDTO.imageUrl != null)
            {
                picasso.load(notificationDTO.imageUrl)
                        .transform(userPhotoTransformation)
                        .into(notificationPicture);
            }
            else
            {
                resetNotificationProfilePicture();
            }
        }
        else
        {
            resetView();
        }
    }

    private void resetView()
    {
        notificationContent.setText(null);
        notificationTime.setText(null);

        resetNotificationProfilePicture();
    }

    private void resetNotificationProfilePicture()
    {
        picasso.cancelRequest(notificationPicture);
        picasso.load(R.drawable.superman_facebook)
                .transform(userPhotoTransformation)
                .into(notificationPicture);
    }

    private void fetchNotification()
    {
        detachNotificationFetchTask();

        notificationFetchTask = notificationCache.getOrFetch(notificationKey, false, notificationFetchListener);
        notificationFetchTask.execute();
    }

    private void detachNotificationFetchTask()
    {
        if (notificationFetchTask != null)
        {
            notificationFetchTask.setListener(null);
        }
        notificationFetchTask = null;
    }


    //<editor-fold desc="Handling click event">
    /**
     * Handle click event on NotificationItemView
     * @return true if handled, false otherwise
     */
    boolean handleNotificationItemClicked()
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
                break;

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
            getNavigator().pushFragment(PushableTimelineFragment.class, bundle);
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
                    getNavigator().pushFragment(PositionListFragment.class, args);
                }
            }
        }
    }
    //</editor-fold>

    private class NotificationFetchListener implements DTOCache.Listener<NotificationKey,NotificationDTO>
    {
        @Override public void onDTOReceived(NotificationKey key, NotificationDTO value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(NotificationKey key, Throwable error)
        {
            THToast.show(new THException(error));
        }
    }

    private void linkWith(NotificationDTO notificationDTO, boolean andDisplay)
    {
        this.notificationDTO = notificationDTO;

        if (andDisplay)
        {
            display(notificationDTO);
        }
    }


    //<editor-fold desc="Navigation">
    protected DashboardNavigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
    }
    //</editor-fold>
}
