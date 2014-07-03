package com.tradehero.th.api.social;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javax.inject.Inject;

public class UserFriendsDTOJacksonModule extends SimpleModule
{
    @Inject public UserFriendsDTOJacksonModule(JsonDeserializer<UserFriendsDTO> userFriendsDTODeserializer)
    {
        super("PolymorphicUserFriendsDTODeserializerModule",
                new Version(1, 0, 0, null, null, null));
        addDeserializer(UserFriendsDTO.class, userFriendsDTODeserializer);
    }
}
