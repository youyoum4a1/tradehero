package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.THSignedNumber;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by tradehero on 14-5-29.
 */
public class LeaderboardFriendsItemView extends RelativeLayout
        implements DTOView<LeaderboardUserDTO>, View.OnClickListener
{
    @InjectView(R.id.leaderboard_user_item_position) TextView lbmuPosition;
    @InjectView(R.id.leaderboard_user_item_profile_picture) ImageView avatar;
    @InjectView(R.id.leaderboard_user_item_display_name) TextView name;
    @InjectView(R.id.lbmu_roi) TextView lbmuRoi;
    @InjectView(R.id.lbmu_roi_annualized) TextView lbmuRoiAnnualized;
    @InjectView(R.id.leaderboard_user_item_country_logo) ImageView countryLogo;

    private LeaderboardUserDTO mLeaderboardUserDTO;
    @Inject CurrentUserId currentUserId;
    @Inject protected Picasso picasso;
    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;

    public LeaderboardFriendsItemView(Context context)
    {
        super(context);
    }

    public LeaderboardFriendsItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeaderboardFriendsItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
        initViews();
    }

    private void initViews()
    {
        //upgradeNow.setOnClickListener(this);
        avatar.setOnClickListener(this);
        loadDefaultPicture();
    }

    protected void loadDefaultPicture()
    {
        if (avatar != null)
        {
            picasso.load(R.drawable.superman_facebook)
                    .transform(peopleIconTransformation)
                    .into(avatar);
        }
    }

    public void displayPicture()
    {
        if (avatar != null)
        {
            loadDefaultPicture();
            if (mLeaderboardUserDTO != null && getPicture() != null)
            {
                picasso.load(getPicture())
                        .transform(peopleIconTransformation)
                        .placeholder(avatar.getDrawable())
                        .into(avatar, new Callback()
                        {
                            @Override public void onSuccess()
                            {
                            }

                            @Override public void onError()
                            {
                                //loadDefaultPicture();
                            }
                        });
            }
        }
    }

    public String getPicture()
    {
        if (mLeaderboardUserDTO != null && mLeaderboardUserDTO.picture != null)
        {
            return mLeaderboardUserDTO.picture;
        }
        else if (mLeaderboardUserDTO != null && mLeaderboardUserDTO.fbPicUrl != null)
        {
            return mLeaderboardUserDTO.fbPicUrl;
        }
        return null;
    }

    @Override public void display(LeaderboardUserDTO dto)
    {
        Timber.d("lyl %s", dto.toString());
        mLeaderboardUserDTO = dto;
        if (mLeaderboardUserDTO != null)
        {
            //updatePosition();
            displayPicture();
            updateName();
            updateROI();
            displayCountryLogo();
        }
    }

    public void displayCountryLogo()
    {
        if (countryLogo != null)
        {
            if (mLeaderboardUserDTO != null && mLeaderboardUserDTO.countryCode != null)
            {
                countryLogo.setImageResource(getCountryLogoId(0, mLeaderboardUserDTO.countryCode));
                countryLogo.setVisibility(VISIBLE);
            }
            else
            {
                //countryLogo.setImageResource(R.drawable.default_image);
                countryLogo.setVisibility(GONE);
            }
        }
    }

    public int getCountryLogoId(int defaultResId, String country)
    {
        try
        {
            return Country.valueOf(country).logoId;
        }
        catch (IllegalArgumentException ex)
        {
            return defaultResId;
        }
    }

    public void updateROI()
    {
        if (mLeaderboardUserDTO.displayName != null)
        {
            THSignedNumber roi = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, mLeaderboardUserDTO.roiInPeriod * 100);
            lbmuRoi.setText(roi.toString());
            lbmuRoi.setTextColor(getResources().getColor(roi.getColor()));

            THSignedNumber roiAnnualizedVal = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE,
                    mLeaderboardUserDTO.roiAnnualizedInPeriod * 100);
            String roiAnnualizedFormat = getContext().getString(R.string.leaderboard_roi_annualized);
            String roiAnnualized = String.format(roiAnnualizedFormat, roiAnnualizedVal.toString());
            lbmuRoiAnnualized.setText(Html.fromHtml(roiAnnualized));
        }
    }

    public void updatePosition(int position)
    {
        if (lbmuPosition != null)
        {
            lbmuPosition.setText("" + (position + 1));
        }
    }

    public void updateName()
    {
        if (mLeaderboardUserDTO.displayName != null)
        {
            if (mLeaderboardUserDTO.displayName.isEmpty())
            {
                name.setText(mLeaderboardUserDTO.firstName + mLeaderboardUserDTO.lastName);
            }
            else {
                name.setText(mLeaderboardUserDTO.displayName);
            }
        }
        else if (mLeaderboardUserDTO.name != null && !mLeaderboardUserDTO.name.isEmpty())
        {
            name.setText(mLeaderboardUserDTO.name);
        }
    }

    @Override public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.leaderboard_user_item_profile_picture:
                handleOpenProfileButtonClicked();
                break;
        }
    }

    private void handleOpenProfileButtonClicked()
    {
        int userId = mLeaderboardUserDTO.id;

        if (currentUserId != null && currentUserId.get() != userId)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(TimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userId);
            DashboardNavigator dashboardNavigator = ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
            if (dashboardNavigator != null)
            {
                dashboardNavigator.pushFragment(PushableTimelineFragment.class, bundle);
            }
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }

    public void setPosition(int position)
    {
        updatePosition(position);
    }
}
