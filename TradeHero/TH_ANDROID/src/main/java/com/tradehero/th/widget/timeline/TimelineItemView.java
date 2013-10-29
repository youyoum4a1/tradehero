package com.tradehero.th.widget.timeline;

import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.text.OnElementClickListener;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.fragments.trade.TradeFragment;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.MarkdownTextView;
import dagger.Lazy;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Named;
import org.ocpsoft.prettytime.PrettyTime;

/** Created with IntelliJ IDEA. User: tho Date: 9/9/13 Time: 4:24 PM Copyright (c) TradeHero */
public class TimelineItemView extends LinearLayout implements
        DTOView<TimelineItem>, OnElementClickListener, Checkable, View.OnClickListener
{
    private static final String TAG = TimelineItemView.class.getName();
    private TextView username;
    private MarkdownTextView content;
    private ImageView avatar;
    private ImageView vendorImage;
    private TextView time;

    @Inject @Named("CurrentUser") protected UserBaseDTO currentUserBase;
    @Inject protected Lazy<Picasso> picasso;
    private boolean checked;
    private Navigator navigator;
    private TimelineItem currentTimelineItem;

    //<editor-fold desc="Constructors">
    public TimelineItemView(Context context)
    {
        super(context, null);
    }

    public TimelineItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs, 0);
    }

    public TimelineItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    //</editor-fold>

    private void init()
    {
        navigator = ((NavigatorActivity) getContext()).getNavigator();
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

        View fbShareButton = findViewById(R.id.timeline_share_facebook);
        if (fbShareButton!=null) fbShareButton.setOnClickListener(new OnClickListener()
        {
            @Override public void onClick(View view)
            {
                THToast.show("Fb share button clicked");
            }
        });

        DaggerUtils.inject(content);
        DaggerUtils.inject(this);
    }

    @Override protected void onFinishInflate()
    {
        init();
    }

    @Override public void display(TimelineItem item)
    {
        UserProfileCompactDTO user = item.getUser();
        if (user == null)
        {
            return;
        }
        currentTimelineItem = item;

        username.setText(user.displayName);

        if (user.picture != null)
        {
            picasso.get()
                    .load(user.picture)
                    .transform(new RoundedShapeTransformation())
                    .into(avatar);
        }

        content.setText(item.getText());

        PrettyTime prettyTime = new PrettyTime(new Date());
        time.setText(prettyTime.format(item.getDate()));

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
                    .into(vendorImage);
        }
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
        navigator.pushFragment(TradeFragment.class, securityId.getArgs());
    }

    private void openUserProfile(int userId)
    {
        Bundle b = new Bundle();
        b.putInt(UserBaseKey.BUNDLE_KEY_KEY, userId);
        b.putBoolean(Navigator.NAVIGATE_FRAGMENT_NO_CACHE, true);

        if (currentUserBase.id != userId)
        {
            navigator.pushFragment(TimelineFragment.class, b, true);
        }
    }

    //<editor-fold desc="android.View.ViewGroup">
    @Override protected int[] onCreateDrawableState(int extraSpace)
    {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked())
        {
            mergeDrawableStates(drawableState, new int[] { android.R.attr.state_checked });
        }
        return drawableState;
    }
    //</editor-fold>

    //<editor-fold desc="android.widget.Checkable">
    @Override public void setChecked(boolean checked)
    {
        if (this.checked != checked)
        {
            this.checked = checked;

            refreshButtonBarVisibility(checked);

            refreshDrawableState();
        }
    }

    private void refreshButtonBarVisibility(boolean checked)
    {
        View buttons = findViewById(R.id.timeline_share_buttons);
        if (checked)
        {
            //buttons.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.reveal_from_top));
            buttons.setVisibility(View.VISIBLE);
        }
        else
        {
            buttons.setVisibility(View.GONE);
            //buttons.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_top));
        }
        // use postInvalidate coz there are more than one item on the listview, each item has its own bottom bar, queue the update
        buttons.postInvalidate();
        THLog.d(TAG, "post invalidation for buttons is submitted");
    }

    @Override public boolean isChecked()
    {
        return checked;
    }

    @Override public void toggle()
    {
        setChecked(!checked);
    }

    //</editor-fold>
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
