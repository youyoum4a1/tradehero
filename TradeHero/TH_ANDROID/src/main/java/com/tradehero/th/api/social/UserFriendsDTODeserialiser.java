package com.tradehero.th.api.social;

import com.tradehero.th.api.UniqueFieldDTODeserialiser;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class UserFriendsDTODeserialiser extends UniqueFieldDTODeserialiser<UserFriendsDTO>
{
    //<editor-fold desc="Constructors">
    @Inject public UserFriendsDTODeserialiser()
    {
        super(createUniqueAttributes(), UserFriendsDTO.class);

    }
    //</editor-fold>

    private static Map<String, Class<? extends UserFriendsDTO>> createUniqueAttributes()
    {
        Map<String, Class<? extends UserFriendsDTO>> uniqueAttributes = new HashMap<>();
        uniqueAttributes.put(UserFriendsFacebookDTO.FACEBOOK_ID, UserFriendsFacebookDTO.class);
        uniqueAttributes.put(UserFriendsLinkedinDTO.LINKEDIN_ID, UserFriendsLinkedinDTO.class);
        uniqueAttributes.put(UserFriendsTwitterDTO.TWITTER_ID, UserFriendsTwitterDTO.class);
        uniqueAttributes.put(UserFriendsWeiboDTO.WEIBO_ID, UserFriendsWeiboDTO.class);
        return uniqueAttributes;
    }

    @Override protected Class<? extends UserFriendsDTO> getDefaultClass()
    {
        return UserFriendsContactEntryDTO.class;
    }
}
