package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.alert.AlertCreateDialogFragment;
import com.tradehero.th.fragments.alert.AlertEditDialogFragment;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionCommentFragment;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionItemViewLinear;
import com.tradehero.th.fragments.news.NewsItemViewHolder;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineItemViewLinear;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.discussion.UserDiscussionAction;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCacheRx;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.VotePair;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;

public class DiscussionFragmentUtil
{
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final DashboardNavigator navigator;
    @NonNull private final Analytics analytics;
    @NonNull private final THRouter thRouter;
    @NonNull private final WatchlistPositionCacheRx watchlistPositionCache;
    @NonNull private final DiscussionServiceWrapper discussionServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public DiscussionFragmentUtil(
            @NonNull CurrentUserId currentUserId,
            @NonNull DashboardNavigator navigator,
            @NonNull Analytics analytics,
            @NonNull THRouter thRouter,
            @NonNull WatchlistPositionCacheRx watchlistPositionCache,
            @NonNull DiscussionServiceWrapper discussionServiceWrapper)
    {
        this.currentUserId = currentUserId;
        this.navigator = navigator;
        this.analytics = analytics;
        this.thRouter = thRouter;
        this.watchlistPositionCache = watchlistPositionCache;
        this.discussionServiceWrapper = discussionServiceWrapper;
    }
    //</editor-fold>

    /**
     * @return an empty observable when the action has been handled. Or one observable with the action when not.
     */
    @NonNull public Observable<UserDiscussionAction> handleUserAction(@NonNull Context context, @NonNull final UserDiscussionAction userAction)
    {
        if (userAction instanceof TimelineItemViewLinear.OpenWatchlistUserAction)
        {
            Bundle args = new Bundle();
            SecurityId securityId = ((TimelineItemViewLinear.OpenWatchlistUserAction) userAction).securityId;
            WatchlistEditFragment.putSecurityId(args, securityId);
            if (watchlistPositionCache.getCachedValue(securityId) != null)
            {
                analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_EditWatchlist));
                ActionBarOwnerMixin.putActionBarTitle(args, context.getString(R.string.watchlist_edit_title));
            }
            else
            {
                analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_CreateWatchlist));
                ActionBarOwnerMixin.putActionBarTitle(args, context.getString(R.string.watchlist_add_title));
            }
            navigator.pushFragment(WatchlistEditFragment.class, args, null);
            return Observable.empty();
        }
        else if (userAction instanceof TimelineItemViewLinear.OpenNewStockAlertUserAction)
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_Alert));
            Fragment currentFragment = navigator.getCurrentFragment();
            if (currentFragment != null)
            {
                DialogFragment dialog = AlertCreateDialogFragment.newInstance(
                        ((TimelineItemViewLinear.OpenNewStockAlertUserAction) userAction).securityId);
                dialog.show(currentFragment.getFragmentManager(), AlertCreateDialogFragment.class.getName());
                return Observable.empty();
            }
            else
            {
                return Observable.just(userAction);
            }
        }
        else if (userAction instanceof TimelineItemViewLinear.UpdateStockAlertUserAction)
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_Alert));
            Fragment currentFragment = navigator.getCurrentFragment();
            if (currentFragment != null)
            {
                DialogFragment dialog = AlertEditDialogFragment.newInstance(
                        ((TimelineItemViewLinear.UpdateStockAlertUserAction) userAction).alertId);
                dialog.show(currentFragment.getFragmentManager(), AlertEditDialogFragment.class.getName());
                return Observable.empty();
            }
            else
            {
                return Observable.just(userAction);
            }
        }
        else if (userAction instanceof TimelineItemViewHolder.SecurityUserAction)
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_BuySell));
            Bundle args = new Bundle();
            SecurityId securityId = ((TimelineItemViewHolder.SecurityUserAction) userAction).securityId;
            BuySellStockFragment.putSecurityId(args, securityId);
            navigator.pushFragment(BuySellStockFragment.class, args);
            return Observable.empty();
        }
        else if (userAction instanceof AbstractDiscussionItemViewHolder.PlayerUserAction)
        {
            Bundle bundle = new Bundle();
            UserBaseKey userClicked = ((AbstractDiscussionItemViewHolder.PlayerUserAction) userAction).userClicked;
            thRouter.save(bundle, userClicked);
            if (currentUserId.toUserBaseKey().equals(userClicked))
            {
                navigator.pushFragment(MeTimelineFragment.class, bundle);
            }
            else
            {
                navigator.pushFragment(PushableTimelineFragment.class, bundle);
            }
            return Observable.empty();
        }
        else if (userAction instanceof NewsItemViewHolder.NewNewsDiscussionAction)
        {
            Bundle args = new Bundle();
            NewsDiscussionFragment.putDiscussionKey(args, userAction.discussionDTO.getDiscussionKey());
            //if(backgroundResourceId > 0)
            //{
            //    NewsDiscussionFragment.putBackgroundResId(args, backgroundResourceId);
            //}

            //if(securityId != null)
            //{
            //    NewsDiscussionFragment.putSecuritySymbol(args, securityId.getSecuritySymbol());
            //}

            navigator.pushFragment(NewsDiscussionFragment.class, args);
            return Observable.empty();
        }
        else if (userAction instanceof TimelineItemViewHolder.TimelineCommentUserAction)
        {
            Bundle args = new Bundle();
            TimelineDiscussionFragment.putDiscussionKey(args, userAction.discussionDTO.getDiscussionKey());
            navigator.pushFragment(TimelineDiscussionFragment.class, args);
            return Observable.empty();
        }
        else if (userAction instanceof SecurityDiscussionItemViewLinear.CommentUserAction)
        {
            Bundle args = new Bundle();
            SecurityDiscussionCommentFragment.putDiscussionKey(args, userAction.discussionDTO.getDiscussionKey());
            if (navigator.getCurrentFragment() == null || !(navigator.getCurrentFragment() instanceof SecurityDiscussionCommentFragment))
            {
                navigator.pushFragment(SecurityDiscussionCommentFragment.class, args);
            }
            Observable.empty();
        }
        else if (userAction instanceof NewsItemViewHolder.OpenWebUserAction)
        {
            NewsItemCompactDTO newsItem = (NewsItemCompactDTO) userAction.discussionDTO;
            if (newsItem.url != null)
            {
                Bundle bundle = new Bundle();
                WebViewFragment.putUrl(bundle, newsItem.url);
                navigator.pushFragment(WebViewFragment.class, bundle);
            }
        }
        else if (userAction instanceof DiscussionActionButtonsView.CommentUserAction)
        {
            Bundle bundle = new Bundle();
            bundle.putBundle(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE, userAction.discussionDTO.getDiscussionKey().getArgs());
            navigator.pushFragment(DiscussionEditPostFragment.class, bundle);
        }
        else if (userAction instanceof VotePair.UserAction)
        {
            DiscussionVoteKey discussionVoteKey = new DiscussionVoteKey(
                    userAction.discussionDTO.getDiscussionKey().getType(),
                    userAction.discussionDTO.id,
                    ((VotePair.UserAction) userAction).voteDirection);
            return discussionServiceWrapper.voteRx(discussionVoteKey)
                    .flatMap(new Func1<DiscussionDTO, Observable<UserDiscussionAction>>()
                    {
                        @Override public Observable<UserDiscussionAction> call(DiscussionDTO discussionDTO)
                        {
                            discussionDTO.populateVote(userAction.discussionDTO);
                            return Observable.empty();
                        }
                    });
        }

        return Observable.just(userAction);
    }
}
