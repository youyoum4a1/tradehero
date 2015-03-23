package com.tradehero.th.api.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.th.api.UniqueFieldDTODeserialiser;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class UserFriendsDTODeserialiser extends UniqueFieldDTODeserialiser<UserFriendsDTO>
{
    //<editor-fold desc="Constructors">
    @Inject public UserFriendsDTODeserialiser(@NotNull ObjectMapper objectMapper)
    {
        super(objectMapper, createUniqueAttributes(), UserFriendsDTO.class);

    }
    //</editor-fold>

    private static Map<String, Class<? extends UserFriendsDTO>> createUniqueAttributes()
    {
        Map<String, Class<? extends UserFriendsDTO>> uniqueAttributes = new HashMap<>();
        uniqueAttributes.put(UserFriendsWeiboDTO.WEIBO_ID, UserFriendsWeiboDTO.class);
        return uniqueAttributes;
    }

    @Override protected Class<? extends UserFriendsDTO> getDefaultClass()
    {
        return UserFriendsContactEntryDTO.class;
    }
}
