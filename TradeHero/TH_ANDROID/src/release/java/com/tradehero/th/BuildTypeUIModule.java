package com.tradehero.th;

import com.tradehero.th.fragments.ForTypographyFragment;
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
    @Provides @ForAchievementListTestingFragment @Override public Class provideAchievementListTestingFragmentClass()
    {
        return null;
    }

    @Provides @ForQuestListTestingFragment @Override public Class provideQuestListTestingFragmentClass()
    {
        return null;
    }

    @Provides @ForXpTestingFragment @Override public Class provideXpTestingFragmentClass()
    {
        return null;
    }

    @Provides @ForTypographyFragment @Override public Class provideTypographyExampleFragment()
    {
        return null;
    }
}
