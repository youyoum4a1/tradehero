package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.market.Exchange;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.THSignedNumber;
import dagger.Lazy;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 12:28 PM To change this template use File | Settings | File Templates. */
public class FollowerListItemView extends RelativeLayout implements DTOView<UserFollowerDTO>,View.OnClickListener
{
    public static final String TAG = FollowerListItemView.class.getName();

    @InjectView(R.id.follower_profile_picture) ImageView userIcon;
    @InjectView(R.id.follower_title) TextView title;
    @InjectView(R.id.follower_revenue) TextView revenueInfo;
    @InjectView(R.id.follower_time) TextView followTime;
    @InjectView(R.id.hint_open_follower_info) ImageView country;



    private UserFollowerDTO userFollowerDTO;
    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;
    @Inject Lazy<Picasso> picasso;
    @Inject PrettyTime prettyTime;

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
        ButterKnife.inject(this);
        DaggerUtils.inject(this);
        if (userIcon != null)
        {
            picasso.get().load(R.drawable.superman_facebook)
                    .transform(peopleIconTransformation)
                    .into(userIcon);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (userIcon != null)
        {
            userIcon.setOnClickListener(this);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        if (userIcon != null)
        {
            userIcon.setOnClickListener(null);
        }
    }


    @Override public void onClick(View v)
    {
        if (v.getId() == R.id.follower_profile_picture)
        {
            if (userFollowerDTO != null)
            {
                handleUserIconClicked();
            }
        }
    }

    private void handleUserIconClicked(){
        THToast.show(String.format("user icon click %s",
                userFollowerDTO.displayName));
        TimelineFragment.viewProfile((DashboardActivity) getContext(), userFollowerDTO.id);
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
        displayFollowing();
        displayCountryLogo();
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

    public void displayFollowing()
    {
        if (followTime != null && userFollowerDTO != null)
        {
            followTime.setText(prettyTime.format(userFollowerDTO.followingSince));
        }
    }

    public void displayCountry()
    {
        if (country != null && userFollowerDTO != null)
        {
            followTime.setText(prettyTime.format(userFollowerDTO.followingSince));
        }
    }

    public void displayCountryLogo()
    {
        if (country != null)
        {
            if (userFollowerDTO != null)
            {
                country.setImageResource(getExchangeLogoId(userFollowerDTO.countryCode));
            }
            else
            {
                country.setImageResource(R.drawable.default_image);
            }
        }
    }

    public int getExchangeLogoId(String country)
    {
        return getExchangeLogoId(0,country);
    }

    public int getExchangeLogoId(int defaultResId,String country)
    {
        try
        {
            return Exchange.valueOf(country).logoId;
        }
        catch (IllegalArgumentException ex)
        {
            return defaultResId;
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
                THSignedNumber thRoiSinceInception = new THSignedNumber(
                        THSignedNumber.TYPE_PERCENTAGE,
                        userFollowerDTO.roiSinceInception * 100);
                revenueInfo.setText(thRoiSinceInception.toString());
                revenueInfo.setTextColor(getContext().getResources().getColor(thRoiSinceInception.getColor()));

                //revenueInfo.setText(String.format(getResources().getString(R.string.manage_followers_revenue_follower), SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, userFollowerDTO.totalRevenue));
            }
            else
            {
                revenueInfo.setText(R.string.na);
            }
        }
    }

    //</editor-fold>
}
