package com.tradehero.th;

import com.tradehero.th.fragments.achievement.AchievementListFragment;
import com.tradehero.th.fragments.achievement.ForAchievementListTestingFragment;
import com.tradehero.th.fragments.achievement.ForQuestListTestingFragment;
import com.tradehero.th.fragments.level.ForXpTestingFragment;
import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
        },

        complete = false,
        library = true,
        overrides = true
)
public class BuildTypeUIModule implements BuildTypeUIModuleExpectation
{
    @Provides @ForAchievementListTestingFragment public Class provideAchievementListTestingFragmentClass()
    {
        return null;
    }

    @Provides @ForQuestListTestingFragment public Class provideQuestListTestingFragmentClass()
    {
        return null;
    }

    @Provides @ForXpTestingFragment public Class provideXpTestingFragmentClass()
    {
        return null;
    }
}
