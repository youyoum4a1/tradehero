package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.SecurityUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class FollowerListItemView extends RelativeLayout
        implements DTOView<FollowerListItemView.DTO>
{
    private static final int INDEX_VIEW_REVENUE = 0;
    private static final int INDEX_VIEW_FREE = 1;

    @Bind(R.id.follower_profile_picture) ImageView userIcon;
    @Bind(R.id.follower_title) TextView title;
    @Bind(R.id.follower_revenue) @Nullable TextView revenueInfo;
    @Bind(R.id.country_logo) ImageView country;
    @Bind(R.id.follower_roi_info) @Nullable TextView roiInfo;
    @Bind(R.id.revenue_switcher) @Nullable ViewSwitcher typeSwitcher;

    @NonNull final PublishSubject<FollowerListItemAdapter.UserAction> userActionSubject;
    @Nullable protected DTO dto;
    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;
    @Inject Lazy<Picasso> picasso;

    //<editor-fold desc="Constructors">
    public FollowerListItemView(Context context)
    {
        super(context);
        userActionSubject = PublishSubject.create();
    }

    public FollowerListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        userActionSubject = PublishSubject.create();
    }

    public FollowerListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        userActionSubject = PublishSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
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
        ButterKnife.bind(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        if (userIcon != null)
        {
            picasso.get().cancelRequest(userIcon);
        }
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick({R.id.follower_profile_picture})
    public void onProfilePictureClicked(View v)
    {
        if (dto != null)
        {
            userActionSubject.onNext(new ProfileUserAction(dto.userFollowerDTO));
        }
    }

    public void display(@NonNull DTO dto)
    {
        this.dto = dto;

        if (userIcon != null)
        {
            picasso.get().load(dto.userFollowerDTO.picture)
                    .transform(peopleIconTransformation)
                    .placeholder(R.drawable.superman_facebook)
                    .error(R.drawable.superman_facebook)
                    .into(userIcon);
        }
        if (country != null)
        {
            country.setImageResource(dto.countryFlagResId);
        }
        if (title != null)
        {
            title.setText(dto.titleText);
        }
        if (revenueInfo != null)
        {
            revenueInfo.setText(dto.revenueText);
        }
        if (roiInfo != null)
        {
            roiInfo.setText(dto.roiInfoText);
        }
        if (typeSwitcher != null)
        {
            typeSwitcher.setDisplayedChild(dto.userFollowerDTO.isFreeFollow ?
                    INDEX_VIEW_FREE :
                    INDEX_VIEW_REVENUE);
        }
    }

    @NonNull public Observable<FollowerListItemAdapter.UserAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
    }

    public static class DTO
    {
        @NonNull public final UserFollowerDTO userFollowerDTO;
        @DrawableRes public final int countryFlagResId;
        public final String titleText;
        public final String revenueText;
        public final Spanned roiInfoText;
        public final String followingSince;
        public boolean isFollowing;

        public DTO(@NonNull Resources resources, @NonNull UserFollowerDTO userFollowerDTO, UserProfileDTO currentUserProfileDTO)
        {
            this.userFollowerDTO = userFollowerDTO;
            countryFlagResId = Country.getCountryLogo(R.drawable.default_image, userFollowerDTO.countryCode);
            titleText = UserBaseDTOUtil.getShortDisplayName(resources, userFollowerDTO);
            revenueText = THSignedMoney.builder(userFollowerDTO.totalRevenue)
                    .currency(SecurityUtils.getDefaultCurrency())
                    .build()
                    .toString();
            roiInfoText = THSignedPercentage
                    .builder(userFollowerDTO.roiSinceInception * 100)
                    .withDefaultColor()
                    .build()
                    .createSpanned();
            followingSince = resources.getString(R.string.manage_heroes_following_since, DateUtils.getDisplayableDate(resources, userFollowerDTO.followingSince, R.string.data_format_dd_mmm_yyyy));
            isFollowing = currentUserProfileDTO.isFollowingUser(userFollowerDTO.getBaseKey());
        }
    }

    @NonNull public static List<DTO> createList(
            @NonNull Resources resources,
            @NonNull Collection<? extends UserFollowerDTO> userFollowerDTOs)
    {
        List<DTO> list = new ArrayList<>();
        for (UserFollowerDTO userFollowerDTO : userFollowerDTOs)
        {
            list.add(new DTO(resources, userFollowerDTO, null));
        }
        return list;
    }

    public static class ProfileUserAction implements FollowerListItemAdapter.UserAction
    {
        @NonNull public final UserFollowerDTO dto;

        public ProfileUserAction(@NonNull UserFollowerDTO dto)
        {
            this.dto = dto;
        }
    }
}
