package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;
import com.tradehero.common.rx.PopupMenuItemClickOperator;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.fragments.discussion.AbstractDiscussionItemViewHolder;
import com.tradehero.th.fragments.discussion.DiscussionActionButtonsView;
import com.tradehero.th.fragments.discussion.TimelineDiscussionFragment;
import com.tradehero.th.fragments.discussion.TimelineItemViewHolder;
import com.tradehero.th.fragments.news.NewsItemViewHolder;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCacheRx;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class TimelineItemViewLinear extends AbstractDiscussionCompactItemViewLinear<TimelineItemDTO>
{
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<WatchlistPositionCacheRx> watchlistPositionCache;
    @Inject Analytics analytics;

    public TimelineItemViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @NonNull @Override protected TimelineItemViewHolder createViewHolder()
    {
        return new TimelineItemViewHolder<>(getContext());
    }

    private void translate()
    {
        socialShareHelper.translate(abstractDiscussionCompactDTO);
    }

    //<editor-fold desc="Popup dialog">
    @NonNull @Override protected Observable<DiscussionActionButtonsView.UserAction> handleUserAction(
            DiscussionActionButtonsView.UserAction userAction)
    {
        if (userAction instanceof DiscussionActionButtonsView.MoreUserAction)
        {
            return createActionPopupMenu()
                    .flatMap(popupMenu -> Observable.create(new PopupMenuItemClickOperator(popupMenu, true)))
                    .map(this::handleMenuItemClicked)
                    .flatMap(result -> Observable.empty());
        }
        if (userAction instanceof DiscussionActionButtonsView.CommentUserAction)
        {
            openTimelineDiscussion();
            return Observable.empty();
        }
        if (userAction instanceof AbstractDiscussionItemViewHolder.PlayerUserAction)
        {
            openOtherTimeline();
            return Observable.empty();
        }
        if (userAction instanceof NewsItemViewHolder.SecurityUserAction)
        {
            openSecurityProfile();
            return Observable.empty();
        }
        return super.handleUserAction(userAction);
    }

    @NonNull private Observable<PopupMenu> createActionPopupMenu()
    {
        PopupMenu popupMenu = new PopupMenu(getContext(), findViewById(R.id.discussion_action_button_more));
        MenuInflater menuInflater = popupMenu.getMenuInflater();

        if (((TimelineItemViewHolder) viewHolder).canShowStockMenu())
        {
            menuInflater.inflate(R.menu.timeline_stock_popup_menu, popupMenu.getMenu());
        }

        return socialShareHelper.canTranslate(abstractDiscussionCompactDTO)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Boolean, PopupMenu>()
                {
                    @Override public PopupMenu call(Boolean canTranslate)
                    {
                        if (canTranslate)
                        {
                            menuInflater.inflate(R.menu.timeline_comment_share_popup_menu, popupMenu.getMenu());
                        }
                        return popupMenu;
                    }
                });
    }

    public boolean handleMenuItemClicked(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.timeline_action_add_to_watchlist:
            {
                openWatchlistEditor();
                return true;
            }

            case R.id.timeline_action_add_alert:
                openStockAlertEditor();
                return true;

            case R.id.timeline_popup_menu_buy_sell:
            {
                openSecurityProfile();
                return true;
            }

            case R.id.timeline_action_translate:
                translate();
                break;
        }
        return false;
    }

    protected SecurityId getSecurityId()
    {
        if (abstractDiscussionCompactDTO instanceof TimelineItemDTO)
        {
            return ((TimelineItemDTO) abstractDiscussionCompactDTO).createFlavorSecurityIdForDisplay();
        }
        return null;
    }
    //</editor-fold>

    protected void openTimelineDiscussion()
    {
        try
        {
            if (discussionKey != null)
            {
                Bundle args = new Bundle();
                TimelineDiscussionFragment.putDiscussionKey(args, discussionKey.getDiscussionKey());
                getNavigator().pushFragment(TimelineDiscussionFragment.class, args);
            }
        }
        catch (java.lang.ClassCastException e)
        {

        }
    }

    protected void openOtherTimeline()
    {
        if (abstractDiscussionCompactDTO instanceof TimelineItemDTO)
        {
            UserProfileCompactDTO user = ((TimelineItemDTO) abstractDiscussionCompactDTO).getUser();
            if (user != null)
            {
                if (currentUserId.get() != user.id)
                {
                    Bundle bundle = new Bundle();
                    TimelineFragment.putUserBaseKey(bundle, new UserBaseKey(user.id));
                    getNavigator().pushFragment(PushableTimelineFragment.class, bundle);
                }
            }
        }
    }

    protected void openSecurityProfile()
    {
        if (abstractDiscussionCompactDTO instanceof TimelineItemDTO)
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_BuySell));

            SecurityMediaDTO flavorSecurityForDisplay = ((TimelineItemDTO) abstractDiscussionCompactDTO).getFlavorSecurityForDisplay();
            if (flavorSecurityForDisplay != null && flavorSecurityForDisplay.securityId != 0)
            {
                SecurityId securityId = new SecurityId(flavorSecurityForDisplay.exchange, flavorSecurityForDisplay.symbol);
                Bundle args = new Bundle();
                BuySellStockFragment.putSecurityId(args, securityId);

                getNavigator().pushFragment(BuySellStockFragment.class, args);
            }
        }
    }

    private void openStockAlertEditor()
    {
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_Alert));

        Bundle args = new Bundle();
        AlertCreateFragment.putSecurityId(args, getSecurityId());
        getNavigator().pushFragment(AlertCreateFragment.class, args);
    }

    private void openWatchlistEditor()
    {
        // TODO make it so that it needs SecurityId
        Bundle args = new Bundle();
        SecurityId securityId = getSecurityId();
        if (securityId != null)
        {
            WatchlistEditFragment.putSecurityId(args, securityId);
            if (watchlistPositionCache.get().getCachedValue(securityId) != null)
            {
                analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_EditWatchlist));
                ActionBarOwnerMixin.putActionBarTitle(args, getContext().getString(R.string.watchlist_edit_title));
            }
            else
            {
                analytics.addEvent(new SimpleEvent(AnalyticsConstants.Monitor_CreateWatchlist));
                ActionBarOwnerMixin.putActionBarTitle(args, getContext().getString(R.string.watchlist_add_title));
            }
        }
        getNavigator().pushFragment(WatchlistEditFragment.class, args, null);
    }
}
