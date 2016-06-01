package com.ayondo.academy.api.social;

import dagger.Module;

@Module(
        injects = {
                UserFriendsDTODeserialiserTest.class,
                UserFriendsDTOFactoryTest.class,
        },
        complete = false,
        library = true
)
public class ApiSocialTestModule
{
}
