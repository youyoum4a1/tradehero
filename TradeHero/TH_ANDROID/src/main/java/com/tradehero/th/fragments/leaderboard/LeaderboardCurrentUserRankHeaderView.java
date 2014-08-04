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
    @InjectView(R.id.leaderboard_current_user_rank_display_name) protected TextView mDisplayName;
    @InjectView(R.id.leaderboard_current_user_rank_profile_picture) protected ImageView mAvatar;
    @InjectView(R.id.leaderboard_current_user_rank_position) protected TextView mCurrentRankLabel;
    @InjectView(R.id.leaderboard_current_user_rank_roi) protected TextView mROILabel;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;

    public static final int FLAG_USER_NOT_RANKED = -1;

    private UserProfileDTO userProfileDTO;

    protected Integer mCurrentRank;
    protected Double mRoiSinceInception = 0.0D;

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
    }

    private void displayUserPhoto()
    {
        if (userProfileDTO != null && userProfileDTO.picture != null)
        {
            picasso.load(userProfileDTO.picture)
                    .transform(peopleIconTransformation)
                    .fit()
                    .centerCrop()
                    .into(mAvatar);
        }
        else
        {
            loadDefaultPicture();
        }
    }

    private void loadDefaultPicture()
    {
        picasso.load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .fit()
                .centerInside()
                .into(mAvatar);
    }

    private void displayUserName()
    {
        if (userProfileDTO != null)
        {
            mDisplayName.setText(userProfileDTO.displayName);
        }
    }

    public void setRank(int rank)
    {
        this.mCurrentRank = rank;
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
        return mCurrentRank != null && mCurrentRank != FLAG_USER_NOT_RANKED && mCurrentRank > 0;
    }

    protected void displayUserIsRanked()
    {
        mCurrentRankLabel.setText(String.valueOf(mCurrentRank));
        //Set the ROI from the user profile cache
        displayROIValue(mRoiSinceInception);
    }

    protected void displayUserNotRanked()
    {
        mCurrentRankLabel.setText("-");
        mROILabel.setText(R.string.leaderboard_not_ranked);
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

        mROILabel.setText(thRoiSinceInception.toString());
        mROILabel.setTextColor(getResources().getColor(thRoiSinceInception.getColorResId()));
    }

    protected DashboardNavigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
    }
}
