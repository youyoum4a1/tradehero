package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.localytics.android.LocalyticsSession;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.graphics.ScaleKeepRatioTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.discussion.AbstractDiscussionItemView;
import com.tradehero.th.fragments.discussion.TimelineDiscussionFragment;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.share.SocialShareTranslationHelper;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.client.Response;

public class TimelineItemView extends AbstractDiscussionItemView<TimelineItemDTOKey>
{
    @InjectView(R.id.timeline_user_profile_name) TextView username;
    @InjectView(R.id.timeline_user_profile_picture) ImageView avatar;
    @InjectView(R.id.timeline_vendor_picture) ImageView vendorImage;
    @InjectView(R.id.in_watchlist_indicator) ImageView watchlistIndicator;

    @InjectView(R.id.discussion_action_button_comment_count) TextView commentCount;
    @InjectView(R.id.discussion_action_button_share) View buttonShare;
    @InjectView(R.id.discussion_action_button_more) View buttonMore;

    @OnClick({
            R.id.timeline_user_profile_name,
            R.id.timeline_user_profile_picture,
            R.id.timeline_vendor_picture,
            R.id.discussion_action_button_comment_count,
            R.id.discussion_action_button_share,
            R.id.discussion_action_button_more,
    })
    public void onItemClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.timeline_user_profile_picture:
            case R.id.timeline_user_profile_name:
                openOtherTimeline();
                break;
            case R.id.discussion_action_button_comment_count:
                openTimelineDiscussion();
                break;
            case R.id.timeline_vendor_picture:
            case R.id.timeline_action_button_trade_wrapper:
                openSecurityProfile();
                break;
            case R.id.discussion_action_button_share:
            case R.id.timeline_action_button_share_wrapper:
                createAndShowSharePopupDialog();
                break;
            case R.id.discussion_action_button_more:
                PopupMenu popUpMenu = createActionPopupMenu();
                popUpMenu.show();
                break;
        }
    }

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<Picasso> picasso;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;
    @Inject Lazy<WatchlistPositionCache> watchlistPositionCache;
    @Inject Lazy<UserWatchlistPositionCache> userWatchlistPositionCache;
    @Inject Lazy<UserTimelineServiceWrapper> userTimelineServiceWrapper;
    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapper;
    @Inject LocalyticsSession localyticsSession;
    @Inject SocialShareTranslationHelper socialShareHelper;

    private TimelineItemDTO timelineItemDTO;
    private MiddleCallback<Response> shareMiddleCallback;

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
        ButterKnife.inject(this);
    }

    private void openTimelineDiscussion()
    {
        if (discussionKey != null)
        {
            Bundle args = new Bundle();
            args.putBundle(TimelineDiscussionFragment.DISCUSSION_KEY_BUNDLE_KEY, discussionKey.getArgs());
            getNavigator().pushFragment(TimelineDiscussionFragment.class, args);
        }
    }

    private void openOtherTimeline()
    {
        if (timelineItemDTO != null)
        {
            UserProfileCompactDTO user = timelineItemDTO.getUser();
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

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        detachShareMiddleCallback();
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    protected void detachShareMiddleCallback()
    {
        if (shareMiddleCallback != null)
        {
            shareMiddleCallback.setPrimaryCallback(null);
        }
        shareMiddleCallback = null;
    }

    //<editor-fold desc="Action Buttons">
    private void updateActionButtonsVisibility()
    {
        buttonMore.setVisibility((socialShareHelper.canTranslate(timelineItemDTO) || canShowStockMenu()) ? View.VISIBLE : View.GONE);
    }
    //</editor-fold>

    @Override protected void linkWith(AbstractDiscussionDTO abstractDiscussionDTO, boolean andDisplay)
    {
        super.linkWith(abstractDiscussionDTO, andDisplay);
        //need do this, cos linkwith is in front of onAttachedToWindow
        ButterKnife.inject(this);
        if (abstractDiscussionDTO instanceof TimelineItemDTO)
        {
            linkWith((TimelineItemDTO) abstractDiscussionDTO, true);
        }
    }

    private void linkWith(TimelineItemDTO timelineItemDTO, boolean andDisplay)
    {
        this.timelineItemDTO = timelineItemDTO;
        if (this.timelineItemDTO == null)
        {
            return;
        }

        UserProfileCompactDTO user = this.timelineItemDTO.getUser();
        if (user == null)
        {
            return;
        }

        // username
        displayUsername(user);

        // user profile picture
        displayUserProfilePicture(user);

        // vendor logo
        displayVendorLogo(this.timelineItemDTO);

        displayWatchlistIndicator();

        updateActionButtons();
    }

    private void updateActionButtons()
    {
        commentCount.setText("" + timelineItemDTO.commentCount);

        updateActionButtonsVisibility();
    }

    private void displayUsername(UserProfileCompactDTO user)
    {
        if (username != null && user != null && user.displayName != null)
        {
            username.setText(user.displayName);
        }
    }

    private void displayUserProfilePicture(UserProfileCompactDTO user)
    {
        if (user.picture != null && picasso != null)
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
        if (avatar != null && picasso != null)
        {
            picasso.get()
                    .load(R.drawable.superman_facebook)
                    .transform(peopleIconTransformation)
                    .into(avatar);
        }
    }

    private void displayVendorLogo(TimelineItemDTO item)
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

    private void translate()
    {
        socialShareHelper.translate(timelineItemDTO);
    }

    private void createAndShowSharePopupDialog()
    {
        socialShareHelper.share(timelineItemDTO);
    }

    private void openSettingScreen()
    {
        getNavigator().pushFragment(SettingsFragment.class);
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.timeline_user_profile_picture:
            case R.id.timeline_user_profile_name:
                if (timelineItemDTO != null)
                {
                    UserProfileCompactDTO user = timelineItemDTO.getUser();
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
                if (timelineItemDTO != null)
                {
                    openSecurityProfile();
                }
                break;
            case R.id.timeline_action_button_share_wrapper:
                createAndShowSharePopupDialog();
                break;
        }
    }

    private void openSecurityProfile()
    {
        if (timelineItemDTO != null)
        {
            localyticsSession.tagEvent(LocalyticsConstants.Monitor_BuySell);

            SecurityMediaDTO flavorSecurityForDisplay = timelineItemDTO.getFlavorSecurityForDisplay();
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

    //<editor-fold desc="Popup dialog">
    private PopupMenu createActionPopupMenu()
    {
        PopupMenu popupMenu = new PopupMenu(getContext(), buttonMore);
        MenuInflater menuInflater = popupMenu.getMenuInflater();

        if (canShowStockMenu())
        {
            menuInflater.inflate(R.menu.timeline_stock_popup_menu, popupMenu.getMenu());
        }

        if (socialShareHelper.canTranslate(timelineItemDTO))
        {
            menuInflater.inflate(R.menu.timeline_comment_share_popup_menu, popupMenu.getMenu());
        }

        popupMenu.setOnMenuItemClickListener(createMonitorPopupMenuItemClickListener());
        return popupMenu;
    }

    protected boolean canShowStockMenu()
    {
        return timelineItemDTO != null && timelineItemDTO.getFlavorSecurityForDisplay() != null;
    }

    private void updateMonitorMenuView(Menu menu)
    {
        if (menu != null)
        {
            SecurityId securityId = getSecurityId();

            MenuItem watchListMenuItem = menu.findItem(R.id.timeline_action_add_to_watchlist);
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
            }
        };
    }

    @Override protected SecurityId getSecurityId()
    {
        if (timelineItemDTO == null)
        {
            return null;
        }
        return timelineItemDTO.createFlavorSecurityIdForDisplay();
    }
    //</editor-fold>
}
