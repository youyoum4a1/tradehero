package com.ayondo.academy.fragments.discussion;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.ayondo.academy.R;
import com.ayondo.academy.api.discussion.DiscussionDTO;
import com.ayondo.academy.api.discussion.key.DiscussionKey;
import com.ayondo.academy.api.discussion.key.DiscussionVoteKey;
import com.ayondo.academy.api.news.NewsItemCompactDTO;
import com.ayondo.academy.api.security.SecurityId;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.fragments.alert.AlertCreateDialogFragment;
import com.ayondo.academy.fragments.alert.AlertEditDialogFragment;
import com.ayondo.academy.fragments.base.ActionBarOwnerMixin;
import com.ayondo.academy.fragments.discussion.stock.SecurityDiscussionCommentFragment;
import com.ayondo.academy.fragments.discussion.stock.SecurityDiscussionItemViewLinear;
import com.ayondo.academy.fragments.security.WatchlistEditFragment;
import com.ayondo.academy.fragments.timeline.PushableTimelineFragment;
import com.ayondo.academy.fragments.web.WebViewFragment;
import com.ayondo.academy.models.discussion.NewNewsDiscussionAction;
import com.ayondo.academy.models.discussion.OpenNewStockAlertUserAction;
import com.ayondo.academy.models.discussion.OpenWatchlistUserAction;
import com.ayondo.academy.models.discussion.OpenWebUserAction;
import com.ayondo.academy.models.discussion.PlayerUserAction;
import com.ayondo.academy.models.discussion.SecurityUserAction;
import com.ayondo.academy.models.discussion.UpdateStockAlertUserAction;
import com.ayondo.academy.models.discussion.UserDiscussionAction;
import com.ayondo.academy.network.service.DiscussionServiceWrapper;
import com.ayondo.academy.persistence.watchlist.WatchlistPositionCacheRx;
import com.ayondo.academy.utils.SecurityUtils;
import com.ayondo.academy.utils.route.THRouter;
import com.ayondo.academy.widget.VotePair;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

public class DiscussionFragmentUtil
{
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final DashboardNavigator navigator;
    //TODO Change Analytics
    //@NonNull private final Analytics analytics;
    @NonNull private final THRouter thRouter;
    @NonNull private final WatchlistPositionCacheRx watchlistPositionCache;
    @NonNull private final DiscussionServiceWrapper discussionServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public DiscussionFragmentUtil(
            @NonNull CurrentUserId currentUserId,
            @NonNull DashboardNavigator navigator,
            @NonNull THRouter thRouter,
            @NonNull WatchlistPositionCacheRx watchlistPositionCache,
            @NonNull DiscussionServiceWrapper discussionServiceWrapper)
    {
        this.currentUserId = currentUserId;
        this.navigator = navigator;
        //TODO Change Analytics
        //Was part of constructor
        //this.analytics = analytics;
        this.thRouter = thRouter;
        this.watchlistPositionCache = watchlistPositionCache;
        this.discussionServiceWrapper = discussionServiceWrapper;
    }
    //</editor-fold>

    /**
     * @return an empty observable when the action has been handled. Or one observable with the action when not.
     */
    @NonNull public Observable<UserDiscussionAction> handleUserAction(@NonNull Activity activity, @NonNull final UserDiscussionAction userAction)
    {
        if (userAction instanceof OpenWatchlistUserAction)
        {
            Bundle args = new Bundle();
            SecurityId securityId = ((OpenWatchlistUserAction) userAction).securityId;
            WatchlistEditFragment.putSecurityId(args, securityId);
            if (watchlistPositionCache.getCachedValue(securityId) != null)
            {
                //TODO Change Analytics
                //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_EditWatchlist));
                ActionBarOwnerMixin.putActionBarTitle(args, activity.getString(R.string.watchlist_edit_title));
            }
            else
            {
                //TODO Change Analytics
                //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_CreateWatchlist));
                ActionBarOwnerMixin.putActionBarTitle(args, activity.getString(R.string.watchlist_add_title));
            }
            navigator.pushFragment(WatchlistEditFragment.class, args, null);
            return Observable.empty();
        }
        else if (userAction instanceof OpenNewStockAlertUserAction)
        {
            //TODO Change Analytics
            //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_Alert));
            Fragment currentFragment = navigator.getCurrentFragment();
            if (currentFragment != null)
            {
                DialogFragment dialog = AlertCreateDialogFragment.newInstance(
                        ((OpenNewStockAlertUserAction) userAction).securityId);
                dialog.show(currentFragment.getFragmentManager(), AlertCreateDialogFragment.class.getName());
                return Observable.empty();
            }
            else
            {
                return Observable.just(userAction);
            }
        }
        else if (userAction instanceof UpdateStockAlertUserAction)
        {
            //TODO Change Analytics
            //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_Alert));
            Fragment currentFragment = navigator.getCurrentFragment();
            if (currentFragment != null)
            {
                DialogFragment dialog = AlertEditDialogFragment.newInstance(
                        ((UpdateStockAlertUserAction) userAction).alertId);
                dialog.show(currentFragment.getFragmentManager(), AlertEditDialogFragment.class.getName());
                return Observable.empty();
            }
            else
            {
                return Observable.just(userAction);
            }
        }
        else if (userAction instanceof SecurityUserAction)
        {
            //TODO Change Analytics
            //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_BuySell));
            SecurityId securityId = ((SecurityUserAction) userAction).securityId;
            if (securityId.getExchange().equals(SecurityUtils.FX_EXCHANGE))
            {
                thRouter.open("fx-security/" + securityId.getExchange() + "/" + securityId.getSecuritySymbol(), activity);
            }
            else
            {
                thRouter.open("stock-security/" + securityId.getExchange() + "/" + securityId.getSecuritySymbol(), activity);
            }
            return Observable.empty();
        }
        else if (userAction instanceof PlayerUserAction)
        {
            UserBaseKey userClicked = ((PlayerUserAction) userAction).userClicked;
            if (currentUserId.toUserBaseKey().equals(userClicked))
            {
                thRouter.open("user/me", activity);
            }
            else
            {
                thRouter.open(PushableTimelineFragment.getUserPath(userClicked), activity);
            }
            return Observable.empty();
        }
        else if (userAction instanceof NewNewsDiscussionAction)
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
        else if (userAction instanceof OpenWebUserAction)
        {
            NewsItemCompactDTO newsItem = (NewsItemCompactDTO) userAction.discussionDTO;
            if (newsItem.url != null)
            {
                Bundle bundle = new Bundle();
                WebViewFragment.putUrl(bundle, newsItem.url);
                navigator.pushFragment(WebViewFragment.class, bundle);
            }
            else
            {
                Timber.e(new NullPointerException(), "We should not have reached here");
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
