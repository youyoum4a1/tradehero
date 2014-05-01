package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.position.GetPositionsCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.client.Response;

public class MiddleCallbackFollowUser extends MiddleCallbackUpdateUserProfile
{
    @Inject HeroListCache heroListCache;
    @Inject GetPositionsCache getPositionsCache;
    @Inject UserMessagingRelationshipCache userMessagingRelationshipCache;
    final UserBaseKey userToFollow;

    public MiddleCallbackFollowUser(UserBaseKey userToFollow, Callback<UserProfileDTO> primaryCallback)
    {
        super(primaryCallback);
        this.userToFollow = userToFollow;
    }

    @Override public void success(UserProfileDTO userProfileDTO, Response response)
    {
        // TODO do it nicer
        heroListCache.invalidate(userToFollow);
        getPositionsCache.invalidate(userToFollow);
        userMessagingRelationshipCache.invalidate(userToFollow);
        super.success(userProfileDTO, response);
    }
}
