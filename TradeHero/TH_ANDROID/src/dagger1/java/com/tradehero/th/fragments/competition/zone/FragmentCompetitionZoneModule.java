package com.ayondo.academy.fragments.competition.zone;

import dagger.Module;

@Module(
        injects = {
                CompetitionZoneListItemView.class,
                CompetitionZoneLeaderboardListItemView.class,
                CompetitionZonePortfolioView.class,
                CompetitionZonePrizePoolView.class,
        },
        library = true,
        complete = false
)
public class FragmentCompetitionZoneModule
{
}
