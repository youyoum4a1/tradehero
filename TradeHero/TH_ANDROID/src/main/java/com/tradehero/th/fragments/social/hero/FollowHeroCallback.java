package com.tradehero.th.fragments.social.hero;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import retrofit.Callback;
import retrofit.client.Response;

abstract public class FollowHeroCallback implements Callback<UserProfileDTO>
{
    protected HeroListCache heroListCache;
    protected UserProfileCache userProfileCache;

    public FollowHeroCallback(HeroListCache heroListCache, UserProfileCache userProfileCache)
    {
        this.heroListCache = heroListCache;
        this.userProfileCache = userProfileCache;
    }

    @Override public void success(UserProfileDTO userProfileDTO, Response response)
    {
        userProfileCache.put(userProfileDTO.getBaseKey(), userProfileDTO);
        heroListCache.invalidate(userProfileDTO.getBaseKey());
    }
}
