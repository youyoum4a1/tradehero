package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.thm.R;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.THSignedNumber;
import javax.inject.Inject;

public class UserProfileCompactViewHolder
{
    @InjectView(R.id.user_profile_avatar) @Optional public ImageView avatar;
    @InjectView(R.id.user_profile_roi) @Optional public TextView roiSinceInception;
    @InjectView(R.id.user_profile_profit_value) @Optional public TextView profitValue;
    @InjectView(R.id.user_profile_followers_count_wrapper) @Optional public View followersCountWrapper;
    @InjectView(R.id.user_profile_followers_count) @Optional public TextView followersCount;
    @InjectView(R.id.user_profile_heroes_count_wrapper) @Optional public View heroesCountWrapper;
    @InjectView(R.id.user_profile_heroes_count)  @Optional public TextView heroesCount;
    @InjectView(R.id.user_profile_display_name) @Optional public TextView displayName;
    @InjectView(R.id.btn_user_profile_default_portfolio) @Optional public ImageView btnDefaultPortfolio;

    @Inject protected Context context;
    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;
    @Inject protected Picasso picasso;
    protected UserProfileDTO userProfileDTO;
    private OnProfileClickedListener profileClickedListener;

    public UserProfileCompactViewHolder(View view)
    {
        super();
        DaggerUtils.inject(this);
        initViews(view);
    }

    public void initViews(View view)
    {
        ButterKnife.inject(this, view);
    }

    public void detachViews()
    {
        ButterKnife.reset(this);
    }

    public void setProfileClickedListener(OnProfileClickedListener profileClickedListener)
    {
        this.profileClickedListener = profileClickedListener;
    }

    public void display(UserProfileDTO dto)
    {
        this.userProfileDTO = dto;
        loadUserPicture();
        displayRoiSinceInception();
        displayProfitValue();
        displayFollowersCount();
        displayHeroesCount();
        displayDisplayName();
    }

    protected void displayProfitValue()
    {
        if (profitValue != null)
        {
            if (userProfileDTO != null && userProfileDTO.portfolio != null)
            {
                Double pl = userProfileDTO.portfolio.plSinceInception;
                if (pl == null)
                {
                    pl = 0.0;
                }
                THSignedNumber thPlSinceInception = new THSignedNumber(
                        THSignedNumber.TYPE_MONEY,
                        pl,
                        THSignedNumber.WITH_SIGN,
                        SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY,
                        THSignedNumber.TYPE_SIGN_PLUS_MINUS_ALWAYS);
                profitValue.setText(thPlSinceInception.toString());
                profitValue.setTextColor(
                        context.getResources().getColor(thPlSinceInception.getColor()));
            }
            else
            {
                profitValue.setText(R.string.na);
                profitValue.setTextColor(context.getResources().getColor(R.color.black));
            }
        }
    }

    protected void loadUserPicture()
    {
        if (avatar != null)
        {
            loadDefaultPicture();
            if (userProfileDTO != null && userProfileDTO.picture != null)
            {
                picasso
                        .load(userProfileDTO.picture)
                        .transform(peopleIconTransformation)
                        .placeholder(avatar.getDrawable())
                        .into(avatar);
            }
        }
    }

    protected void loadDefaultPicture()
    {
        if (avatar != null)
        {
            picasso
                    .load(R.drawable.superman_facebook)
                    .transform(peopleIconTransformation)
                    .into(avatar);
        }
    }

    protected void displayRoiSinceInception()
    {
        if (roiSinceInception != null)
        {
            if (userProfileDTO != null
                    && userProfileDTO.portfolio != null
                    && userProfileDTO.portfolio.roiSinceInception != null)
            {
                THSignedNumber thRoiSinceInception = new THSignedNumber(
                        THSignedNumber.TYPE_PERCENTAGE,
                        userProfileDTO.portfolio.roiSinceInception * 100);
                roiSinceInception.setText(thRoiSinceInception.toString());
                roiSinceInception.setTextColor(
                        context.getResources().getColor(thRoiSinceInception.getColor()));
            }
            else
            {
                roiSinceInception.setText(R.string.na);
            }
        }
    }

    protected void displayFollowersCount()
    {
        if (followersCount != null)
        {
            if (userProfileDTO != null)
            {
                followersCount.setText(Integer.toString(userProfileDTO.allFollowerCount));
            }
            else
            {
                followersCount.setText(R.string.na);
            }
        }
    }

    protected void displayHeroesCount()
    {
        if (heroesCount != null)
        {
            if (userProfileDTO != null)
            {
                heroesCount.setText(Integer.toString(
                        userProfileDTO.heroIds == null ? userProfileDTO.allHeroCount : userProfileDTO.heroIds.size()));
            }
            else
            {
                heroesCount.setText(R.string.na);
            }
        }
    }

    protected void displayDisplayName()
    {
        if (displayName != null)
        {
            if (userProfileDTO != null)
            {
                displayName.setText(userProfileDTO.displayName);
            }
            else
            {
                displayName.setText(R.string.na);
            }
        }
    }

    @OnClick({R.id.user_profile_heroes_count, R.id.user_profile_heroes_count_wrapper}) @Optional
    protected void notifyHeroClicked()
    {
        OnProfileClickedListener listener = profileClickedListener;
        if (listener != null)
        {
            listener.onHeroClicked();
        }
    }

    @OnClick({R.id.user_profile_followers_count, R.id.user_profile_followers_count_wrapper}) @Optional
    protected void notifyFollowerClicked()
    {
        OnProfileClickedListener listener = profileClickedListener;
        if (listener != null)
        {
            listener.onFollowerClicked();
        }
    }

    @OnClick(R.id.btn_user_profile_default_portfolio) @Optional
    protected void notifyDefaultPortfolioClicked()
    {
        OnProfileClickedListener listener = profileClickedListener;
        if (listener != null)
        {
            listener.onDefaultPortfolioClicked();
        }
    }

    public static interface OnProfileClickedListener
    {
        void onHeroClicked();
        void onFollowerClicked();
        void onDefaultPortfolioClicked();
    }
}
