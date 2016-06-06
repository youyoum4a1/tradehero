package com.androidth.general.fragments.contestcenter;

import dagger.Module;

@Module(
        injects = {
                ContestCenterFragment.class,
                ContestCenterBaseFragment.class,
                ContestCenterActiveFragment.class,
                ContestCenterJoinedFragment.class,

                ContestItemAdapter.class,
                ContestContentView.class,
                ContestCompetitionView.class,
        },
        library = true,
        complete = false
)
public class FragmentContestCenterModule
{
}
