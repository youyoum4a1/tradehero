package com.tradehero.th.widget.timeline;

import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.text.OnElementClickListener;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.misc.MediaDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.base.THUser;
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
public class TimelineItemView extends RelativeLayout implements
        DTOView<TimelineItem>, OnElementClickListener, Checkable, View.OnClickListener
{
    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };
    private TextView username;
    private MarkdownTextView content;
    private ImageView avatar;
    private ImageView vendorImage;
    private TextView time;

    @Inject @Named("CurrentUser") protected UserBaseDTO currentUserBase;
    @Inject protected Lazy<Picasso> picasso;
    private boolean checked;
    private Navigator navigator;
    private int userId;

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

        setOnFocusChangeListener(new OnFocusChangeListener()
        {
            @Override public void onFocusChange(View view, boolean focused)
            {
                View buttons = view.findViewById(R.id.timeline_share_buttons);
                if (focused)
                {
                    //buttons.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_bottom));
                    buttons.setVisibility(View.VISIBLE);
                }
                else
                {
                    //buttons.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_bottom));
                    buttons.setVisibility(View.GONE);
                }
            }
        });

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

        userId = user.id;
        username.setText(user.displayName);

        if (user.picture != null)
        {
            picasso.get().load(user.picture)
                    .placeholder(R.drawable.superman_facebook)
                    .into(avatar);
        }

        content.setText(item.getText());

        PrettyTime prettyTime = new PrettyTime(new Date());
        time.setText(prettyTime.format(item.getDate()));

        MediaDTO firstMediaWithLogo = item.firstMediaWithLogo();
        if (firstMediaWithLogo != null)
        {
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
                SecurityId securityId = new SecurityId(matchStrings[1], matchStrings[2]);
                navigator.pushFragment(TradeFragment.class, securityId.getArgs());
                break;

            case "link":
                if (matchStrings.length < 3) break;
                String link = matchStrings[2];

                break;
        }
    }

    private void openUserProfile(int userId)
    {
        Bundle b = new Bundle();
        b.putInt(TimelineFragment.BUNDLE_KEY_USER_ID, userId);

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
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
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
            refreshDrawableState();
        }
    }

    @Override public boolean isChecked()
    {
        return checked;
    }

    @Override public void toggle()
    {
        setChecked(!checked);
    }

    @Override public void onClick(View view)
    {
        openUserProfile(userId);
    }
    //</editor-fold>
}
