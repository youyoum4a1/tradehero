package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import javax.inject.Inject;

public class FollowerListItemView extends RelativeLayout
        implements DTOView<UserFollowerDTO>
{
    @InjectView(R.id.follower_profile_picture) ImageView userIcon;
    @InjectView(R.id.follower_title) TextView title;
    @InjectView(R.id.follower_revenue) @Optional TextView revenueInfo;
    @InjectView(R.id.country_logo) ImageView country;

    protected UserFollowerDTO userFollowerDTO;
    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;
    @Inject Lazy<Picasso> picasso;
    @Inject UserBaseDTOUtil userBaseDTOUtil;
    @Inject THRouter thRouter;
    @Inject DashboardNavigator navigator;

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
        HierarchyInjector.inject(this);
        if (userIcon != null && !isInEditMode())
        {
            picasso.get().load(R.drawable.superman_facebook)
                    .transform(peopleIconTransformation)
                    .into(userIcon);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick({R.id.follower_profile_picture})
    public void onProfilePictureClicked(View v)
    {
        if (userFollowerDTO != null)
        {
            pushTimelineFragment();
        }
    }

    private void pushTimelineFragment()
    {
        Bundle bundle = new Bundle();
        thRouter.save(bundle, new UserBaseKey(userFollowerDTO.id));
        navigator.pushFragment(PushableTimelineFragment.class, bundle);
    }

    public void display(UserFollowerDTO followerDTO)
    {
        this.userFollowerDTO = followerDTO;
        display();
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayUserIcon();
        displayCountryLogo();
        displayTitle();
        displayRevenue();
    }

    public void displayUserIcon()
    {

        displayDefaultUserIcon();

        if (userIcon != null && userFollowerDTO != null)
        {
            picasso.get().load(userFollowerDTO.picture)
                    .transform(peopleIconTransformation)
                            //TODO if this view is reused, userIcon.getDrawable() may returns the different drawable
                    .placeholder(userIcon.getDrawable())
                    .error(R.drawable.superman_facebook)
                    .into(userIcon);
        }
    }

    public void displayDefaultUserIcon()
    {
        picasso.get().load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .into(userIcon);
    }

    public void displayCountryLogo()
    {
        if (country != null)
        {
            int imageResId = R.drawable.default_image;
            if (userFollowerDTO != null)
            {
                imageResId = Country.getCountryLogo(R.drawable.default_image, userFollowerDTO.countryCode);
            }
            country.setImageResource(imageResId);
        }
    }

    public void displayTitle()
    {
        if (title != null)
        {
            title.setText(userBaseDTOUtil.getShortDisplayName(getContext(), userFollowerDTO));
        }
    }

    public void displayRevenue()
    {
        if (revenueInfo != null)
        {
            if (userFollowerDTO != null)
            {
                THSignedNumber revenue = THSignedMoney.builder(userFollowerDTO.totalRevenue)
                        .currency(SecurityUtils.getDefaultCurrency())
                        .build();
                revenueInfo.setText(revenue.toString());
            }
            else
            {
                revenueInfo.setText(R.string.na);
            }
        }
    }
    //</editor-fold>
}
