package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;
import com.localytics.android.LocalyticsSession;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.fragments.discussion.TimelineDiscussionFragment;
import com.tradehero.th.fragments.discussion.TimelineItemViewHolder;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import dagger.Lazy;
import javax.inject.Inject;

public class TimelineItemViewLinear extends AbstractDiscussionCompactItemViewLinear<TimelineItemDTOKey>
{
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<WatchlistPositionCache> watchlistPositionCache;
    @Inject LocalyticsSession localyticsSession;

    //<editor-fold desc="Constructors">
    public TimelineItemViewLinear(Context context)
    {
        super(context);
    }

    public TimelineItemViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TimelineItemViewLinear(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }

    @Override protected TimelineItemViewHolder createViewHolder()
    {
        return new TimelineItemViewHolder<TimelineItemDTO>();
    }

    @Override protected void linkWith(AbstractDiscussionCompactDTO abstractDiscussionDTO, boolean andDisplay)
    {
        super.linkWith(abstractDiscussionDTO, andDisplay);
        if (andDisplay)
        {
        }
    }

    protected PopupMenu.OnMenuItemClickListener createMonitorPopupMenuItemClickListener()
    {
        return new MonitorPopupMenuItemClickListener();
    }

    protected class MonitorPopupMenuItemClickListener implements PopupMenu.OnMenuItemClickListener
    {
        @Override public boolean onMenuItemClick(MenuItem item)
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
    }

    private void translate()
    {
        socialShareHelper.translate(abstractDiscussionCompactDTO);
    }

    //<editor-fold desc="Popup dialog">
    private PopupMenu createActionPopupMenu()
    {
        PopupMenu popupMenu = new PopupMenu(getContext(), findViewById(R.id.discussion_action_button_more));
        MenuInflater menuInflater = popupMenu.getMenuInflater();

        if (((TimelineItemViewHolder) viewHolder).canShowStockMenu())
        {
            menuInflater.inflate(R.menu.timeline_stock_popup_menu, popupMenu.getMenu());
        }

        if (socialShareHelper.canTranslate(abstractDiscussionCompactDTO))
        {
            menuInflater.inflate(R.menu.timeline_comment_share_popup_menu, popupMenu.getMenu());
        }

        popupMenu.setOnMenuItemClickListener(createMonitorPopupMenuItemClickListener());
        return popupMenu;
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
        if (discussionKey != null)
        {
            Bundle args = new Bundle();
            TimelineDiscussionFragment.putDiscussionKey(args, discussionKey);
            getNavigator().pushFragment(TimelineDiscussionFragment.class, args);
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
                    bundle.putInt(TimelineFragment.BUNDLE_KEY_SHOW_USER_ID, user.id);
                    getNavigator().pushFragment(PushableTimelineFragment.class, bundle);
                }
            }
        }
    }

    protected void openSecurityProfile()
    {
        if (abstractDiscussionCompactDTO instanceof TimelineItemDTO)
        {
            localyticsSession.tagEvent(LocalyticsConstants.Monitor_BuySell);

            SecurityMediaDTO flavorSecurityForDisplay = ((TimelineItemDTO) abstractDiscussionCompactDTO).getFlavorSecurityForDisplay();
            if (flavorSecurityForDisplay != null && flavorSecurityForDisplay.securityId != 0)
            {
                SecurityId securityId = new SecurityId(flavorSecurityForDisplay.exchange, flavorSecurityForDisplay.symbol);
                Bundle args = new Bundle();
                args.putBundle(
                        BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE,
                        securityId.getArgs());

                getNavigator().pushFragment(BuySellFragment.class, args);
            }
        }
    }

    private void openStockAlertEditor()
    {
        localyticsSession.tagEvent(LocalyticsConstants.Monitor_Alert);

        Bundle args = new Bundle();
        args.putBundle(AlertCreateFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, getSecurityId().getArgs());
        getNavigator().pushFragment(AlertCreateFragment.class, args);
    }

    private void openWatchlistEditor()
    {
        Bundle args = new Bundle();
        SecurityId securityId = getSecurityId();
        if (securityId != null)
        {
            WatchlistEditFragment.putSecurityId(args, securityId);
            if (watchlistPositionCache.get().get(securityId) != null)
            {
                localyticsSession.tagEvent(LocalyticsConstants.Monitor_EditWatchlist);
                DashboardFragment.putActionBarTitle(args, getContext().getString(R.string.watchlist_edit_title));
            }
            else
            {
                localyticsSession.tagEvent(LocalyticsConstants.Monitor_CreateWatchlist);
                DashboardFragment.putActionBarTitle(args, getContext().getString(R.string.watchlist_add_title));
            }
        }
        getNavigator().pushFragment(WatchlistEditFragment.class, args, Navigator.PUSH_UP_FROM_BOTTOM);
    }

    @Override
    protected AbstractDiscussionCompactItemViewHolder.OnMenuClickedListener createViewHolderMenuClickedListener()
    {
        return new TimelineItemViewMenuClickedListener()
        {
            @Override public void onShareButtonClicked()
            {
                // Nothing to do
            }

            @Override public void onTranslationRequested()
            {
                // Nothing to do
            }
        };
    }

    abstract protected class TimelineItemViewMenuClickedListener extends AbstractDiscussionViewHolderClickedListener
        implements TimelineItemViewHolder.OnMenuClickedListener
    {
        @Override public void onMoreButtonClicked()
        {
            PopupMenu popUpMenu = createActionPopupMenu();
            popUpMenu.show();
        }

        @Override public void onCommentButtonClicked()
        {
            openTimelineDiscussion();
        }

        @Override public void onUserClicked(UserBaseKey userClicked)
        {
            openOtherTimeline();
        }

        @Override public void onSecurityClicked()
        {
            openSecurityProfile();
        }
    }
}
