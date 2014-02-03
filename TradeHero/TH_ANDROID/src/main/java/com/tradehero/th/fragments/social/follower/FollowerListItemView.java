package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.SecurityUtils;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 12:28 PM To change this template use File | Settings | File Templates. */
public class FollowerListItemView extends RelativeLayout implements DTOView<UserFollowerDTO>
{
    public static final String TAG = FollowerListItemView.class.getName();

    private ImageView userIcon;
    private TextView title;
    private TextView revenueInfo;

    private UserFollowerDTO userFollowerDTO;
    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;
    @Inject Lazy<Picasso> picasso;

    //<editor-fold desc="Constructors">
    public FollowerListItemView(Context context)
    {
        super(context);
    }

    public FollowerListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FollowerListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initViews();
        DaggerUtils.inject(this);
        if (userIcon != null)
        {
            picasso.get().load(R.drawable.superman_facebook)
                    .transform(peopleIconTransformation)
                    .into(userIcon);
        }
    }

    private void initViews()
    {
        userIcon = (ImageView) findViewById(R.id.follower_profile_picture);
        title = (TextView) findViewById(R.id.follower_title);
        revenueInfo = (TextView) findViewById(R.id.follower_revenue);
    }

    public UserFollowerDTO getUserFollowerDTO()
    {
        return userFollowerDTO;
    }

    public void display(UserFollowerDTO followerDTO)
    {
        linkWith(followerDTO, true);
    }

    public void linkWith(UserFollowerDTO followerDTO, boolean andDisplay)
    {
        this.userFollowerDTO = followerDTO;
        if (andDisplay)
        {
            displayUserIcon();
            displayTitle();
            displayRevenue();
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayUserIcon();
        displayTitle();
        displayRevenue();
    }

    public void displayUserIcon()
    {
        if (userIcon != null)
        {
            if (userFollowerDTO != null)
            {
                picasso.get().load(userFollowerDTO.picture)
                             .transform(peopleIconTransformation)
                             .into(userIcon);
            }
        }
    }

    public void displayTitle()
    {
        if (title != null)
        {
            title.setText(UserBaseDTOUtil.getLongDisplayName(getContext(), userFollowerDTO));
        }
    }

    public void displayRevenue()
    {
        if (revenueInfo != null)
        {
            if (userFollowerDTO != null)
            {
                revenueInfo.setText(String.format(getResources().getString(R.string.manage_followers_revenue_follower), SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, userFollowerDTO.totalRevenue));
            }
            else
            {
                revenueInfo.setText(R.string.na);
            }
        }
    }
    //</editor-fold>
}
