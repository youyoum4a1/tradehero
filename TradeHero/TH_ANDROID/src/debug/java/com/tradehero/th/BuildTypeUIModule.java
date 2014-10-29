package com.tradehero.th;

import com.tradehero.th.fragments.DebugFragmentModule;
import com.tradehero.th.fragments.ForTypographyFragment;
import com.tradehero.th.fragments.TypographyExampleFragment;
import com.tradehero.th.fragments.achievement.AchievementListTestingFragment;
import com.tradehero.th.fragments.achievement.ForAchievementListTestingFragment;
import com.tradehero.th.fragments.achievement.ForQuestListTestingFragment;
import com.tradehero.th.fragments.achievement.QuestListTestingFragment;
import com.tradehero.th.fragments.level.ForXpTestingFragment;
import com.tradehero.th.fragments.level.XpTestingFragment;
import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                DebugFragmentModule.class,
        },

        complete = false,
        library = true,
        overrides = true
)
public class BuildTypeUIModule implements BuildTypeUIModuleExpectation
{
    @Provides @ForAchievementListTestingFragment @Override public Class provideAchievementListTestingFragmentClass()
    {
        return AchievementListTestingFragment.class;
    }

    @Provides @ForQuestListTestingFragment @Override public Class provideQuestListTestingFragmentClass()
    {
        return QuestListTestingFragment.class;
    }

    @Provides @ForXpTestingFragment @Override public Class provideXpTestingFragmentClass()
    {
        return XpTestingFragment.class;
    }

    @Provides @ForTypographyFragment @Override public Class provideTypographyExampleFragment()
    {
        return TypographyExampleFragment.class;
    }
}
