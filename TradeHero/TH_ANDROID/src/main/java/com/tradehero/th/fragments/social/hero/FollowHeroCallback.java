package com.tradehero.th.fragments.social.hero;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import retrofit.Callback;
import retrofit.client.Response;

/** Created with IntelliJ IDEA. User: xavier Date: 11/29/13 Time: 7:28 PM To change this template use File | Settings | File Templates. */
abstract public class FollowHeroCallback implements Callback<UserProfileDTO>
{
    public static final String TAG = FollowHeroCallback.class.getSimpleName();

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
