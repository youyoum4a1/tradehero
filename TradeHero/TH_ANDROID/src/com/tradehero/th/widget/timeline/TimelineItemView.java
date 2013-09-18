package com.tradehero.th.widget.timeline;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.local.TimelineItem;
import com.tradehero.th.api.misc.MediaDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.widget.MarkdownTextView;
import java.util.Date;
import org.ocpsoft.prettytime.PrettyTime;

/** Created with IntelliJ IDEA. User: tho Date: 9/9/13 Time: 4:24 PM Copyright (c) TradeHero */
public class TimelineItemView extends RelativeLayout implements DTOView<TimelineItem>
{
    private TextView username;
    private MarkdownTextView content;
    private ImageView avatar;
    private ImageView vendorImage;
    private TextView time;

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
        time = (TextView) findViewById(R.id.timeline_time);
        vendorImage = (ImageView) findViewById(R.id.timeline_vendor_picture);
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
            Picasso.with(getContext())
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
            Picasso.with(getContext())
                    .load(firstMediaWithLogo.url)
                    .transform(new WhiteToTransparentTransformation())
                    .into(vendorImage);
        }
    }

}
