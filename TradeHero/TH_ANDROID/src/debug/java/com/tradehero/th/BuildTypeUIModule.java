package com.tradehero.th;

import com.tradehero.th.fragments.DebugFragmentModule;
import com.tradehero.th.fragments.achievement.AchievementListDebugFragment;
import com.tradehero.th.fragments.achievement.AchievementListTestingFragment;
import com.tradehero.th.fragments.achievement.ForAchievementListFragment;
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
    @Provides @ForAchievementListFragment @Override public Class provideAchievementListFragmentClass()
    {
        return AchievementListDebugFragment.class;
    }

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
}
