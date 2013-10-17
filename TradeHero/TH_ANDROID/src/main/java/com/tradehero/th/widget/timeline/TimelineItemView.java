package com.tradehero.th.widget.timeline;

import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.text.OnElementClickListener;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.misc.MediaDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.fragments.trade.AbstractTradeFragment;
import com.tradehero.th.fragments.trade.TradeFragment;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.MarkdownTextView;
import java.util.Date;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

/** Created with IntelliJ IDEA. User: tho Date: 9/9/13 Time: 4:24 PM Copyright (c) TradeHero */
public class TimelineItemView extends RelativeLayout implements DTOView<TimelineItem>, OnElementClickListener
{
    private TextView username;
    private MarkdownTextView content;
    private ImageView avatar;
    private ImageView vendorImage;
    private TextView time;

    @Inject protected Picasso picasso;

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
        username = (TextView) findViewById(R.id.timeline_user_profile_name);
        avatar = (ImageView) findViewById(R.id.timeline_user_profile_picture);
        content = (MarkdownTextView) findViewById(R.id.timeline_item_content);
        content.setMovementMethod(LinkMovementMethod.getInstance());
        content.setOnElementClickListener(this);

        time = (TextView) findViewById(R.id.timeline_time);
        vendorImage = (ImageView) findViewById(R.id.timeline_vendor_picture);

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
        if (user != null)
        {
            username.setText(user.displayName);
            picasso
                    .load(user.picture)
                    .transform(new RoundedShapeTransformation())
                    .into(avatar);
        }
        content.setText(item.getText());

        PrettyTime prettyTime = new PrettyTime(new Date());
        time.setText(prettyTime.format(item.getDate()));

        MediaDTO firstMediaWithLogo = item.firstMediaWithLogo();
        if (firstMediaWithLogo != null)
        {
            picasso
                    .load(firstMediaWithLogo.url)
                    .transform(new WhiteToTransparentTransformation())
                    .into(vendorImage);
        }
    }

    @Override public void onClick(View textView, String data, String key, String[] matchStrings)
    {
        Navigator navigator = ((NavigatorActivity) getContext()).getNavigator();
        switch (key)
        {
            case "user":
                int userId = Integer.parseInt(matchStrings[2]);
                Bundle b = new Bundle();
                b.putInt(TimelineFragment.USER_ID, userId);

                if (THUser.getCurrentUserBase().id != userId)
                {
                    navigator.pushFragment(TimelineFragment.class, b, true);
                }
                break;
            case "security":
                if (matchStrings.length < 3) break;
                SecurityId securityId = new SecurityId(matchStrings[1], matchStrings[2]);
                navigator.pushFragment(TradeFragment.class, securityId.getArgs());
                break;
        }
    }
}
