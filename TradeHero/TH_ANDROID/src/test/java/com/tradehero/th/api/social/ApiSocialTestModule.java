package com.tradehero.th.api.social;

import com.tradehero.th.api.security.SecurityCompactDTODeserialiserTest;
import dagger.Module;

@Module(
        injects = {
                PaginatedUserFriendsDTOListDeserialiserTest.class
        },
        complete = false,
        library = true
)
public class ApiSocialTestModule
{
}
