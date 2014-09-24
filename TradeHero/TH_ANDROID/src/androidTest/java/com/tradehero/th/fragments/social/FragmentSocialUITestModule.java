package com.tradehero.th.fragments.social;

import dagger.Module;

@Module(
        injects = {
                PeopleSearchFragmentTest.class,
                PublicPeopleSearchFragment.class,
        },
        complete = false,
        library = true
)
public class FragmentSocialUITestModule
{
}
