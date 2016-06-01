package com.ayondo.academy.fragments.social.follower;

import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.Spanned;
import com.ayondo.academy.R;
import com.ayondo.academy.api.market.Country;
import com.ayondo.academy.api.social.UserFollowerDTO;
import com.ayondo.academy.api.users.UserBaseDTOUtil;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.models.number.THSignedMoney;
import com.ayondo.academy.models.number.THSignedPercentage;
import com.ayondo.academy.utils.DateUtils;
import com.ayondo.academy.utils.SecurityUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FollowerDisplayDTO
{
    @NonNull public final UserFollowerDTO userFollowerDTO;
    @DrawableRes public final int countryFlagResId;
    public final String titleText;
    public final String revenueText;
    public final Spanned roiInfoText;
    public final String followingSince;
    public boolean isFollowing;

    public FollowerDisplayDTO(@NonNull Resources resources, @NonNull UserFollowerDTO userFollowerDTO, UserProfileDTO currentUserProfileDTO)
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
        followingSince = resources.getString(R.string.manage_heroes_following_since,
                DateUtils.getDisplayableDate(resources, userFollowerDTO.followingSince, R.string.data_format_dd_mmm_yyyy));
        isFollowing = currentUserProfileDTO.isFollowingUser(userFollowerDTO.getBaseKey());
    }

    @NonNull public static List<FollowerDisplayDTO> createList(
            @NonNull Resources resources,
            @NonNull Collection<? extends UserFollowerDTO> userFollowerDTOs)
    {
        List<FollowerDisplayDTO> list = new ArrayList<>();
        for (UserFollowerDTO userFollowerDTO : userFollowerDTOs)
        {
            list.add(new FollowerDisplayDTO(resources, userFollowerDTO, null));
        }
        return list;
    }
}
