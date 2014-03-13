package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.localytics.android.LocalyticsSession;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.graphics.ScaleKeepRatioTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.network.service.UserTimelineService;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.LocalyticsConstants;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.Callback;
import retrofit.client.Response;

/** Created with IntelliJ IDEA. User: tho Date: 9/9/13 Time: 4:24 PM Copyright (c) TradeHero */
public class TimelineItemView extends LinearLayout implements
        DTOView<TimelineItem>, View.OnClickListener
{
    @InjectView(R.id.timeline_user_profile_name) TextView username;
    @InjectView(R.id.timeline_item_content) TextView content;
    @InjectView(R.id.timeline_user_profile_picture) ImageView avatar;
    @InjectView(R.id.timeline_vendor_picture) ImageView vendorImage;
    @InjectView(R.id.timeline_time) TextView time;

    @InjectView(R.id.timeline_action_button_trade_wrapper) View tradeActionButton;
    @InjectView(R.id.timeline_action_button_share_wrapper) View shareActionButton;
    @InjectView(R.id.timeline_action_button_monitor_wrapper) View monitorActionButton;
    @InjectView(R.id.in_watchlist_indicator) ImageView watchlistIndicator;

    @Inject Provider<PrettyTime> prettyTime;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<Picasso> picasso;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;
    @Inject Lazy<WatchlistPositionCache> watchlistPositionCache;
    @Inject Lazy<UserWatchlistPositionCache> userWatchlistPositionCache;
    @Inject Lazy<UserTimelineService> userTimelineService;
    @Inject LocalyticsSession localyticsSession;

    private TimelineItem currentTimelineItem;
    private PopupMenu sharePopupMenu;
    private PopupMenu monitorPopupMenu;

    //<editor-fold desc="Constructors">
    public TimelineItemView(Context context)
    {
        super(context);
    }

    public TimelineItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TimelineItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    private void init()
    {
        ButterKnife.inject(this);
        DaggerUtils.inject(content);
        DaggerUtils.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (content != null)
        {
            content.setMovementMethod(LinkMovementMethod.getInstance());
        }
        View[] actionButtons = new View[]
                {username, avatar, vendorImage, tradeActionButton, shareActionButton, monitorActionButton};
        for (View actionButton : actionButtons)
        {
            if (actionButton != null)
            {
                actionButton.setOnClickListener(this);
            }
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        View[] actionButtons = new View[]
                {username, avatar, vendorImage, tradeActionButton, shareActionButton, monitorActionButton};
        for (View actionButton : actionButtons)
        {
            if (actionButton != null)
            {
                actionButton.setOnClickListener(null);
            }
        }

        if (monitorPopupMenu != null)
        {
            monitorPopupMenu.setOnMenuItemClickListener(null);
        }

        displayDefaultUserProfilePicture();

        super.onDetachedFromWindow();
    }

    //<editor-fold desc="Action Buttons">
    private void updateActionButtonsVisibility()
    {
        // hide/show optional action buttons
        List<SecurityMediaDTO> medias = currentTimelineItem.getMedias();
        int visibility = medias != null && medias.size() > 0 ? VISIBLE : INVISIBLE;
        tradeActionButton.setVisibility(visibility);
        monitorActionButton.setVisibility(visibility);
    }
    //</editor-fold>

    @Override public void display(TimelineItem item)
    {
        UserProfileCompactDTO user = item.getUser();
        if (user == null)
        {
            return;
        }
        currentTimelineItem = item;

        if (user.id != currentUserId.get())
        {
            shareActionButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            shareActionButton.setVisibility(View.VISIBLE);
        }
        // username
        displayUsername(user);

        // user profile picture
        displayUserProfilePicture(user);

        // markup text
        displayMarkupText(item);

        // timeline time
        displayTimelineTime(item);

        // vendor logo
        displayVendorLogo(item);

        displayWatchlistIndicator();

        updateActionButtonsVisibility();
    }

    private void displayUsername(UserProfileCompactDTO user)
    {
        username.setText(user.displayName);
    }

    private void displayUserProfilePicture(UserProfileCompactDTO user)
    {
        if (user.picture != null)
        {
            displayDefaultUserProfilePicture();
            picasso.get()
                    .load(user.picture)
                    .transform(peopleIconTransformation)
                    .placeholder(avatar.getDrawable())
                    .into(avatar);
        }
    }

    private void displayDefaultUserProfilePicture()
    {
        picasso.get()
                .load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .into(avatar);
    }

    private void displayMarkupText(TimelineItem item)
    {
        content.setText(item.getText());
    }

    private void displayTimelineTime(TimelineItem item)
    {
        time.setText(prettyTime.get().formatUnrounded(item.getDate()));
    }

    private void displayVendorLogo(TimelineItem item)
    {
        SecurityMediaDTO firstMediaWithLogo = item.getFlavorSecurityForDisplay();
        if (firstMediaWithLogo != null && firstMediaWithLogo.url != null)
        {
            if (vendorImage != null && firstMediaWithLogo.securityId != 0)
            {
                vendorImage.setContentDescription(String.format("%s:%s", firstMediaWithLogo.exchange, firstMediaWithLogo.symbol));
            }
            picasso.get()
                    .load(firstMediaWithLogo.url)
                    .transform(new WhiteToTransparentTransformation())
                    .transform(new ScaleKeepRatioTransformation(
                            0,
                            getResources().getDimensionPixelSize(R.dimen.timeline_vendor_logo_height),
                            getResources().getDimensionPixelSize(R.dimen.timeline_vendor_logo_max_width),
                            getResources().getDimensionPixelSize(R.dimen.timeline_vendor_logo_max_height)))
                    .into(vendorImage);
            vendorImage.setVisibility(VISIBLE);
        }
        else
        {
            vendorImage.setVisibility(GONE);
        }
    }

    private void displayWatchlistIndicator()
    {
        if (watchlistIndicator == null)
        {
            return;
        }

        if (watchlistPositionCache.get().get(getSecurityId()) != null)
        {
            watchlistIndicator.setVisibility(View.VISIBLE);
        }
        else
        {
            watchlistIndicator.setVisibility(View.INVISIBLE);
        }
    }

    private PopupMenu.OnMenuItemClickListener monitorPopupMenuClickListener = new PopupMenu.OnMenuItemClickListener()
    {
        @Override public boolean onMenuItemClick(MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.timeline_popup_menu_monitor_add_to_watch_list:
                {
                    openWatchlistEditor();
                    return true;
                }

                case R.id.timeline_popup_menu_monitor_enable_stock_alert:
                    openStockAlertEditor();
                    return true;

                case R.id.timeline_popup_menu_monitor_view_graph:
                {
                    openStockInfo();
                    return true;
                }
            }
            return false;
        }
    };

    private void openStockInfo()
    {
        localyticsSession.tagEvent(LocalyticsConstants.Monitor_Chart);

        Bundle args = new Bundle();
        SecurityId securityId = getSecurityId();
        if (securityId != null)
        {
            args.putBundle(StockInfoFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        }
        getNavigator().pushFragment(StockInfoFragment.class, args);
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
            args.putBundle(WatchlistEditFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
            if (watchlistPositionCache.get().get(securityId) != null)
            {
                localyticsSession.tagEvent(LocalyticsConstants.Monitor_EditWatchlist);
                args.putString(WatchlistEditFragment.BUNDLE_KEY_TITLE, getContext().getString(R.string.watchlist_edit_title));
            }
            else
            {
                localyticsSession.tagEvent(LocalyticsConstants.Monitor_CreateWatchlist);
                args.putString(WatchlistEditFragment.BUNDLE_KEY_TITLE, getContext().getString(R.string.watchlist_add_title));
            }
        }
        getNavigator().pushFragment(WatchlistEditFragment.class, args, Navigator.PUSH_UP_FROM_BOTTOM);
    }

    private PopupMenu.OnMenuItemClickListener sharePopupMenuClickListener = new PopupMenu.OnMenuItemClickListener()
    {
        @Override public boolean onMenuItemClick(MenuItem item)
        {
            SocialNetworkEnum socialNetworkEnum = null;
            switch (item.getItemId())
            {
                case R.id.timeline_popup_menu_share_facebook:
                    socialNetworkEnum = SocialNetworkEnum.FB;
                    break;
                case R.id.timeline_popup_menu_share_twitter:
                    socialNetworkEnum = SocialNetworkEnum.TW;
                    break;
                case R.id.timeline_popup_menu_share_linked_in:
                    socialNetworkEnum = SocialNetworkEnum.LN;
                    break;
            }
            if (socialNetworkEnum == null)
            {
                return false;
            }

            userTimelineService.get().shareTimelineItem(
                    currentUserId.get(),
                    currentTimelineItem.getTimelineItemId(), new TimelineItemShareRequestDTO(socialNetworkEnum),
                    createShareRequestCallback(socialNetworkEnum));
            return true;
        }
    };

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.timeline_user_profile_picture:
            case R.id.timeline_user_profile_name:
                if (currentTimelineItem != null)
                {
                    UserProfileCompactDTO user = currentTimelineItem.getUser();
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
                break;
            case R.id.timeline_vendor_picture:
            case R.id.timeline_action_button_trade_wrapper:
                if (currentTimelineItem != null)
                {
                    openSecurityProfile();
                }
                break;
            case R.id.timeline_action_button_share_wrapper:
                createAndShowSharePopupMenu();
                break;

            case R.id.timeline_action_button_monitor_wrapper:
                createAndShowMonitorPopupMenu();
                break;
        }
    }

    private void openSecurityProfile()
    {
        localyticsSession.tagEvent(LocalyticsConstants.Monitor_BuySell);

        SecurityMediaDTO flavorSecurityForDisplay = currentTimelineItem.getFlavorSecurityForDisplay();
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

    //<editor-fold desc="Popup dialog">
    private void createAndShowMonitorPopupMenu()
    {
        if (monitorPopupMenu == null)
        {
            monitorPopupMenu = createMonitorPopupMenu();
        }
        updateMonitorMenuView(monitorPopupMenu.getMenu());
        monitorPopupMenu.show();
    }

    private PopupMenu createMonitorPopupMenu()
    {
        PopupMenu popupMenu = new PopupMenu(getContext(), monitorActionButton);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.timeline_monitor_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(monitorPopupMenuClickListener);
        return popupMenu;
    }

    private void createAndShowSharePopupMenu()
    {
        if (sharePopupMenu == null)
        {
            sharePopupMenu = createSharePopupMenu();
        }
        sharePopupMenu.show();
    }

    private PopupMenu createSharePopupMenu()
    {
        PopupMenu popupMenu = new PopupMenu(getContext(), shareActionButton);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.timeline_share_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(sharePopupMenuClickListener);
        return popupMenu;
    }

    private void updateMonitorMenuView(Menu menu)
    {
        if (menu != null)
        {
            SecurityId securityId = getSecurityId();

            MenuItem watchListMenuItem = menu.findItem(R.id.timeline_popup_menu_monitor_add_to_watch_list);
            if (watchListMenuItem != null)
            {
                if (securityId != null)
                {
                    // if Watchlist milestone has finished receiving data
                    if (userWatchlistPositionCache.get().get(currentUserId.toUserBaseKey()) != null)
                    {
                        if (watchlistPositionCache.get().get(securityId) == null)
                        {
                            watchListMenuItem.setTitle(getContext().getString(R.string.watchlist_add_title));
                        }
                        else
                        {
                            watchListMenuItem.setTitle(getContext().getString(R.string.watchlist_edit_title));
                        }
                        watchListMenuItem.setVisible(true);
                    }
                }
                else
                {
                    watchListMenuItem.setVisible(false);
                }
            }
        }
    }

    private Callback<Response> createShareRequestCallback(final SocialNetworkEnum socialNetworkEnum)
    {
        return new THCallback<Response>()
        {
            @Override protected void success(Response response, THResponse thResponse)
            {
                THToast.show(String.format(getContext().getString(R.string.timeline_post_to_social_network), socialNetworkEnum.getName()));
            }

            @Override protected void failure(THException ex)
            {
                THToast.show(String.format(getContext().getString(R.string.timeline_link_account), socialNetworkEnum.getName()));
                //THToast.show(ex);
            }
        };
    }

    private SecurityId getSecurityId()
    {
        if (currentTimelineItem == null || currentTimelineItem.getFlavorSecurityForDisplay() == null)
        {
            return null;
        }

        return new SecurityId(currentTimelineItem.getFlavorSecurityForDisplay().exchange, currentTimelineItem.getFlavorSecurityForDisplay().symbol);
    }
    //</editor-fold>

    //<editor-fold desc="Navigations">
    private DashboardNavigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
    }
    //</editor-fold>
}
