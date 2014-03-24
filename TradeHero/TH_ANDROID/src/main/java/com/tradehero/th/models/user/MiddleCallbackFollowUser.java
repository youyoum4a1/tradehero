package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.social.HeroListCache;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.client.Response;

/**
 * Created by xavier on 3/24/14.
 */
public class MiddleCallbackFollowUser extends MiddleCallbackUpdateUserProfile
{
    @Inject HeroListCache heroListCache;
    @Inject GetPositionsCache getPositionsCache;
    final UserBaseKey userToFollow;

    public MiddleCallbackFollowUser(UserBaseKey userToFollow, Callback<UserProfileDTO> primaryCallback)
    {
        super(primaryCallback);
        this.userToFollow = userToFollow;
    }

    @Override public void success(UserProfileDTO userProfileDTO, Response response)
    {
        heroListCache.invalidate(userToFollow);
        getPositionsCache.invalidate(userToFollow);
        super.success(userProfileDTO, response);
    }
}
