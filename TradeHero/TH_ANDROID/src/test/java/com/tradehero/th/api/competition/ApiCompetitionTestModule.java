package com.tradehero.th.api.competition;

import dagger.Module;

@Module(
        injects = {
                ProviderUtilTest.class,
                CompetitionDTORestrictionComparatorTest.class,
        },
        complete = false,
        library = true
)
public class ApiCompetitionTestModule
{
}
