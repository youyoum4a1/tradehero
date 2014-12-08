package com.tradehero.th.fragments.contestcenter;

import dagger.Component;

@Component
public interface FragmentContestCenterComponent
{
    void injectContestCenterFragment(ContestCenterFragment contestCenterFragment);
    void injectContestCenterBaseFragment(ContestCenterBaseFragment contestCenterBaseFragment);
    void injectContestCenterActiveFragment(ContestCenterActiveFragment contestCenterActiveFragment);
    void injectContestCenterJoinedFragment(ContestCenterJoinedFragment contestCenterJoinedFragment);

    void injectContestItemAdapter(ContestItemAdapter contestItemAdapter);
    void injectContestContentView(ContestContentView contestContentView);
    void injectContestCompetitionView(ContestCompetitionView contestCompetitionView);
}
