package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.common.graphics.ScaleKeepRatioTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.text.OnElementClickListener;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.MarkdownTextView;
import dagger.Lazy;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

/** Created with IntelliJ IDEA. User: tho Date: 9/9/13 Time: 4:24 PM Copyright (c) TradeHero */
public class TimelineItemView extends LinearLayout implements
        DTOView<TimelineItem>, OnElementClickListener, View.OnClickListener
{
    private static final String TAG = TimelineItemView.class.getName();
    private TextView username;
    private MarkdownTextView content;
    private ImageView avatar;
    private ImageView vendorImage;
    private TextView time;

    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    @Inject protected Lazy<Picasso> picasso;
    private TimelineItem currentTimelineItem;
    private View tradeActionButton;
    private View shareActionButton;
    private View monitorActionButton;

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
        if (username != null)
        {
            username.setOnClickListener(this);
        }

        avatar = (ImageView) findViewById(R.id.timeline_user_profile_picture);
        if (avatar != null)
        {
            avatar.setOnClickListener(this);
        }

        content = (MarkdownTextView) findViewById(R.id.timeline_item_content);
        content.setMovementMethod(LinkMovementMethod.getInstance());
        content.setOnElementClickListener(this);

        time = (TextView) findViewById(R.id.timeline_time);

        vendorImage = (ImageView) findViewById(R.id.timeline_vendor_picture);
        if (vendorImage != null)
        {
            vendorImage.setOnClickListener(this);
        }

        initActionButtons();

        DaggerUtils.inject(content);
        DaggerUtils.inject(this);
    }

    private void initActionButtons()
    {
        tradeActionButton = findViewById(R.id.timeline_action_button_trade_wrapper);
        shareActionButton = findViewById(R.id.timeline_action_button_share_wrapper);
        monitorActionButton = findViewById(R.id.timeline_action_button_monitor_wrapper);

        View[] actionButtons = new View[]
                { tradeActionButton, shareActionButton, monitorActionButton };
        for (View actionButton: actionButtons)
        {
            if (actionButton!=null)
            {
                actionButton.setOnClickListener(this);
            }
        }
    }

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

        refreshActionButtons();
    }

    private void refreshActionButtons()
    {
        // hide/show optional action buttons
        List<SecurityMediaDTO> medias = currentTimelineItem.getMedias();
        int visibility = medias != null && medias.size() > 0 ? VISIBLE : INVISIBLE;
        tradeActionButton.setVisibility(visibility);
        monitorActionButton.setVisibility(visibility);
    }

    @Override public void onClick(View textView, String data, String key, String[] matchStrings)
    {
        switch (key)
        {
            case "user":
                int userId = Integer.parseInt(matchStrings[2]);
                openUserProfile(userId);
                break;
            case "security":
                if (matchStrings.length < 3) break;
                String exchange = matchStrings[1];
                String symbol = matchStrings[2];
                openSecurityProfile(exchange, symbol);
                break;

            case "link":
                if (matchStrings.length < 3) break;
                String link = matchStrings[2];

                break;
        }
    }

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
        b.putInt(UserBaseKey.BUNDLE_KEY_KEY, userId);
        b.putBoolean(Navigator.NAVIGATE_FRAGMENT_NO_CACHE, true);

        if (currentUserBaseKeyHolder.getCurrentUserBaseKey().key != userId)
        {
            getNavigator().pushFragment(TimelineFragment.class, b, true);
        }
    }

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
        }
    }
}
