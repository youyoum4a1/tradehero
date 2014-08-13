package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LeaderboardCurrentUserRankHeaderView extends RelativeLayout
    implements DTOView<LeaderboardUserDTO>
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

    @Nullable protected OwnedPortfolioId applicablePortfolioId;
    @Nullable protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    @Nullable protected UserProfileDTO userProfileDTO;
    @Nullable protected LeaderboardUserDTO leaderboardUserDTO;

    //<editor-fold desc="Constructors">
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
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        userProfileCacheListener = createUserProfileCacheListener();
        if (!isInEditMode())
        {
            fetchUserProfile();
            display();
        }
    }

    public void onDestroyView()
    {
        ButterKnife.reset(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        detachUserProfileCache();
        userProfileCacheListener = null;
        super.onDetachedFromWindow();
    }

    protected void fetchUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    protected void detachUserProfileCache()
    {
        userProfileCache.unregister(userProfileCacheListener);
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new LeaderboardCurrentViewUserProfileCacheListener();
    }

    protected class LeaderboardCurrentViewUserProfileCacheListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            userProfileDTO = value;
            display();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }

    public void setApplicablePortfolioId(@Nullable OwnedPortfolioId applicablePortfolioId)
    {
        this.applicablePortfolioId = applicablePortfolioId;
    }

    @Override public void display(LeaderboardUserDTO dto)
    {
        this.leaderboardUserDTO = dto;
        display();
    }

    protected void display()
    {
        displayUserName();
        displayUserPhoto();
        displayUserCountry();
        displayRanking();
        displayROIValue();
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

    protected void displayUserName()
    {
        if (userProfileDTO != null)
        {
            displayName.setText(userProfileDTO.displayName);
        }
    }

    protected boolean isUserRanked()
    {
        return leaderboardUserDTO != null && leaderboardUserDTO.ordinalPosition > -1;
    }

    protected void displayRanking()
    {
        currentRankLabel.setText(getRankLabel());
    }

    protected String getRankLabel()
    {
        if (!isUserRanked())
        {
            return "-";
        }
        if (leaderboardUserDTO.ordinalPosition >= LeaderboardMarkUserItemView.MAX_OWN_RANKING)
        {
            return getContext().getString(R.string.leaderboard_not_ranked_position);
        }
        return THSignedNumber.builder(leaderboardUserDTO.ordinalPosition + 1).build().toString();
    }

    protected void displayROIValue()
    {
        int colorResId = R.color.black;
        String roiString = getContext().getString(R.string.leaderboard_not_ranked);
        if (isUserRanked())
        {
            THSignedNumber thRoiSinceInception = THSignedPercentage.builder(leaderboardUserDTO.roiInPeriod * 100).build();
            roiString = thRoiSinceInception.toString();
            colorResId = thRoiSinceInception.getColorResId();
        }
        roiLabel.setText(roiString);
        roiLabel.setTextColor(getResources().getColor(colorResId));
    }

    @OnClick(R.id.leaderboard_current_user_rank_profile_picture)
    protected void handleAvatarClicked()
    {
        DashboardNavigator navigator = getNavigator();
        if (navigator != null && userProfileDTO != null)
        {
            Bundle args = new Bundle();
            if (applicablePortfolioId != null)
            {
                PushableTimelineFragment.putApplicablePortfolioId(args, applicablePortfolioId);
            }
            PushableTimelineFragment.putUserBaseKey(args, userProfileDTO.getBaseKey());
            navigator.pushFragment(PushableTimelineFragment.class, args);
        }
    }

    protected DashboardNavigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
    }
}
