package com.tradehero.th.api.users;

import com.tradehero.th.api.users.signup.ApiUsersSignUpTestModule;
import dagger.Module;

@Module(
        includes = {
                ApiUsersSignUpTestModule.class,
        },
        injects = {
        },
        complete = false,
        library = true
)
public class ApiUsersTestModule
{
}
