package com.tradehero.th.fragments.social.hero;

import dagger.Module;

@Module(
        injects = {
                HeroRecyclerItemAdapter.class,
                HeroesFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentSocialHeroModule
{
}
