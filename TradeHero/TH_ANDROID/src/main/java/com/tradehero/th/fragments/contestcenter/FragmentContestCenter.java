package com.tradehero.th.fragments.contestcenter;

import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
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
public class FragmentContestCenter
{
}
