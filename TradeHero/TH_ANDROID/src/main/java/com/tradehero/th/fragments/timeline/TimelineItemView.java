package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.RoundedShapeTransformation;
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
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserTimelineService;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.MarkdownTextView;
import dagger.Lazy;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.Callback;
import retrofit.client.Response;

/** Created with IntelliJ IDEA. User: tho Date: 9/9/13 Time: 4:24 PM Copyright (c) TradeHero */
public class TimelineItemView extends LinearLayout implements
        DTOView<TimelineItem>, View.OnClickListener
{
    private static final String TAG = TimelineItemView.class.getName();
    private TextView username;
    private MarkdownTextView content;
    private ImageView avatar;
    private ImageView vendorImage;
    private TextView time;

    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    @Inject protected Lazy<Picasso> picasso;
    @Inject protected Lazy<UserTimelineService> userTimelineService;

    private TimelineItem currentTimelineItem;
    private View tradeActionButton;
    private View shareActionButton;
    private View monitorActionButton;
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
        init();
    }
    //</editor-fold>


    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    private void init()
    {
        username = (TextView) findViewById(R.id.timeline_user_profile_name);
        avatar = (ImageView) findViewById(R.id.timeline_user_profile_picture);
        content = (MarkdownTextView) findViewById(R.id.timeline_item_content);
        time = (TextView) findViewById(R.id.timeline_time);
        vendorImage = (ImageView) findViewById(R.id.timeline_vendor_picture);

        tradeActionButton = findViewById(R.id.timeline_action_button_trade_wrapper);
        shareActionButton = findViewById(R.id.timeline_action_button_share_wrapper);
        monitorActionButton = findViewById(R.id.timeline_action_button_monitor_wrapper);

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
                { username, avatar, vendorImage, tradeActionButton, shareActionButton, monitorActionButton };
        for (View actionButton: actionButtons)
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
                { username, avatar, vendorImage, tradeActionButton, shareActionButton, monitorActionButton };
        for (View actionButton: actionButtons)
        {
            if (actionButton != null)
            {
                actionButton.setOnClickListener(null);
            }
        }
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

        // username
        username.setText(user.displayName);

        // user profile picture
        if (user.picture != null)
        {
            picasso.get()
                    .load(user.picture)
                    .transform(new RoundedShapeTransformation())
                    .into(avatar);
        }

        // markup text
        content.setText(item.getText());

        // timeline time
        PrettyTime prettyTime = new PrettyTime(new Date());
        time.setText(prettyTime.format(item.getDate()));

        // vendor logo
        SecurityMediaDTO firstMediaWithLogo = item.getFirstMediaWithLogo();
        if (firstMediaWithLogo != null)
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
        }

        updateActionButtonsVisibility();
    }

    private PopupMenu.OnMenuItemClickListener monitorPopupMenuClickListener = new PopupMenu.OnMenuItemClickListener()
    {
        @Override public boolean onMenuItemClick(MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.timeline_popup_menu_monitor_add_to_watch_list:
                    return true;

                case R.id.timeline_popup_menu_monitor_enable_stock_alert:
                    return true;

                case R.id.timeline_popup_menu_monitor_view_graph:
                    Bundle args = new Bundle();
                    SecurityId securityId = getSecurityId();
                    if (securityId != null)
                    {
                        args.putBundle(StockInfoFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
                    }
                    getNavigator().pushFragment(StockInfoFragment.class, args);
                    return true;
            }
            return false;
        }
    };

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
                    socialNetworkEnum = SocialNetworkEnum.LI;
                    break;
            }
            if (socialNetworkEnum == null)
            {
                return false;
            }

            userTimelineService.get().shareTimelineItem(
                    currentUserBaseKeyHolder.getCurrentUserBaseKey().key,
                    currentTimelineItem.getId(), new TimelineItemShareRequestDTO(socialNetworkEnum),
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
                        openUserProfile(user.id);
                    }
                }
                break;
            case R.id.timeline_vendor_picture:
            case R.id.timeline_action_button_trade_wrapper:
                if (currentTimelineItem != null)
                {
                    SecurityMediaDTO firstMediaWithLogo = currentTimelineItem.getFirstMediaWithLogo();
                    if (firstMediaWithLogo != null && firstMediaWithLogo.securityId != 0)
                    {
                        openSecurityProfile(firstMediaWithLogo.exchange, firstMediaWithLogo.symbol);
                    }
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

    //<editor-fold desc="Popup dialog">
    private void createAndShowMonitorPopupMenu()
    {
        if (monitorPopupMenu == null)
        {
            monitorPopupMenu = createMonitorPopupMenu();
        }
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
                THToast.show(String.format(getContext().getString(R.string.link_account), socialNetworkEnum.getName()));
                //THToast.show(ex);
            }
        };
    }

    private SecurityId getSecurityId()
    {
        if (currentTimelineItem == null || currentTimelineItem.getFirstMediaWithLogo() == null)
        {
            return null;
        }

        return new SecurityId(currentTimelineItem.getFirstMediaWithLogo().exchange, currentTimelineItem.getFirstMediaWithLogo().symbol);
    }
    //</editor-fold>


    //<editor-fold desc="Navigations">
    private void openSecurityProfile(String exchange, String symbol)
    {
        SecurityId securityId = new SecurityId(exchange, symbol);
        getNavigator().pushFragment(BuySellFragment.class, securityId.getArgs());
    }

    private Navigator getNavigator()
    {
        return ((NavigatorActivity) getContext()).getNavigator();
    }

    private void openUserProfile(int userId)
    {
        Bundle b = new Bundle();
        b.putInt(TimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userId);
        b.putBoolean(Navigator.NAVIGATE_FRAGMENT_NO_CACHE, true);

        if (currentUserBaseKeyHolder.getCurrentUserBaseKey().key != userId)
        {
            getNavigator().pushFragment(TimelineFragment.class, b, true);
        }
    }
    //</editor-fold>


}
