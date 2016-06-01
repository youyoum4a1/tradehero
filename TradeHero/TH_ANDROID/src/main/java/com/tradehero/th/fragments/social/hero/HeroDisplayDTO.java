package com.ayondo.academy.fragments.social.hero;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.api.social.HeroDTO;
import com.ayondo.academy.api.users.UserBaseDTOUtil;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.models.number.THSignedPercentage;
import com.ayondo.academy.utils.DateUtils;

public class HeroDisplayDTO
{
    @NonNull public final UserBaseKey followerId;
    @NonNull public final HeroDTO heroDTO;
    @NonNull public final String titleText;
    @NonNull public final String followingSince;
    @NonNull public final CharSequence roiInfo;
    public boolean isCurrentUserFollowing;

    public HeroDisplayDTO(@NonNull Resources resources,
            @NonNull UserBaseKey followerId,
            @NonNull HeroDTO heroDTO,
            @NonNull boolean isCurrentUserFollowing)
    {
        this.followerId = followerId;
        this.heroDTO = heroDTO;
        this.titleText = UserBaseDTOUtil.getShortDisplayName(resources, heroDTO);

        if (heroDTO.followingSince != null)
        {
            followingSince = String.format(
                    resources.getString(R.string.manage_heroes_following_since),
                    DateUtils.getDisplayableDate(resources, heroDTO.followingSince, R.string.data_format_dd_mmm_yyyy)
            );
        }
        else
        {
            followingSince = resources.getString(R.string.na);
        }

        if (heroDTO.roiSinceInception != null)
        {
            roiInfo = THSignedPercentage.builder(heroDTO.roiSinceInception * 100)
                    .withDefaultColor()
                    .build()
                    .createSpanned();
        }
        else
        {
            roiInfo = resources.getString(R.string.na);
        }

        this.isCurrentUserFollowing = isCurrentUserFollowing;
    }
}
