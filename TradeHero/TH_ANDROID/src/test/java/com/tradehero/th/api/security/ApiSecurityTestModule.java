package com.ayondo.academy.api.security;

import dagger.Module;

@Module(
        injects = {
                SecurityCompactDTODeserialiserTest.class,
        },
        complete = false,
        library = true
)
public class ApiSecurityTestModule
{
}
