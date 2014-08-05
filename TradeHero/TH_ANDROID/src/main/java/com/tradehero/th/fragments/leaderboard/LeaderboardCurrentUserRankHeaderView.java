package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.models.number.THSignedNumber;
import javax.inject.Inject;
import org.jetbrains.annotations.Nullable;

public class LeaderboardCurrentUserRankHeaderView extends RelativeLayout
{
    @InjectView(R.id.leaderboard_current_user_rank_display_name) protected TextView displayName;
    @InjectView(R.id.leaderboard_current_user_rank_profile_picture) protected ImageView avatar;
    @InjectView(R.id.leaderboard_current_user_rank_position) protected TextView currentRankLabel;
    @InjectView(R.id.leaderboard_current_user_rank_roi) protected TextView roiLabel;
    @InjectView(R.id.leaderboard_current_user_rank_country_logo) protected ImageView countryLogo;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;

    public static final int FLAG_USER_NOT_RANKED = -1;

    protected UserProfileDTO userProfileDTO;

    protected Integer currentRank;
    protected Double roiInPeriod = 0.0D;

    public LeaderboardCurrentUserRankHeaderView(Context context)
    {
        super(context);
    }

    public LeaderboardCurrentUserRankHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeaderboardCurrentUserRankHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        DaggerUtils.inject(this);

        init();
        display();
    }

    private void init()
    {
        if (!isInEditMode())
        {
            userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        }
    }

    private void display()
    {
        displayUserName();
        displayUserPhoto();
        displayUserCountry();
    }

    private void displayUserPhoto()
    {
        if (userProfileDTO != null && userProfileDTO.picture != null)
        {
            picasso.load(userProfileDTO.picture)
                    .transform(peopleIconTransformation)
                    .fit()
                    .centerCrop()
                    .into(avatar);
        }
        else
        {
            loadDefaultPicture();
        }
    }

    private void displayUserCountry()
    {
        if (shouldDisplayCountryLogo())
        {
            int imageResId = R.drawable.default_image;
            if (userProfileDTO != null)
            {
                imageResId = Country.getCountryLogo(R.drawable.default_image, userProfileDTO.countryCode);
            }
            picasso.load(imageResId)
                    .fit()
                    .centerInside()
                    .into(countryLogo);
        }
    }

    protected boolean shouldDisplayCountryLogo()
    {
        return true;
    }

    private void loadDefaultPicture()
    {
        picasso.load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .fit()
                .centerInside()
                .into(avatar);
    }

    private void displayUserName()
    {
        if (userProfileDTO != null)
        {
            displayName.setText(userProfileDTO.displayName);
        }
    }

    public void setRank(int rank)
    {
        this.currentRank = rank;
        if (isUserRanked())
        {
            displayUserIsRanked();
        }
        else
        {
            displayUserNotRanked();
        }
    }

    protected boolean isUserRanked()
    {
        return currentRank != null && currentRank != FLAG_USER_NOT_RANKED && currentRank > 0;
    }

    protected void displayUserIsRanked()
    {
        currentRankLabel.setText(String.valueOf(currentRank));
        displayROIValue(roiInPeriod);
    }

    protected void displayUserNotRanked()
    {
        currentRankLabel.setText("-");
        roiLabel.setText(R.string.leaderboard_not_ranked);
    }

    public void setRoiToBeShown(@Nullable Double roiToBeShown)
    {
        if (roiToBeShown != null && isUserRanked())
        {
            displayROIValue(roiToBeShown);
        }
    }

    private void displayROIValue(double value)
    {
        THSignedNumber thRoiSinceInception = THSignedPercentage.builder(value * 100).build();

        roiLabel.setText(thRoiSinceInception.toString());
        roiLabel.setTextColor(getResources().getColor(thRoiSinceInception.getColorResId()));
    }

    protected DashboardNavigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
    }
}
