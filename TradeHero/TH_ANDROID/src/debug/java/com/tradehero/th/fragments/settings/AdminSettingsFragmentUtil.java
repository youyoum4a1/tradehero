package com.ayondo.academy.fragments.settings;

import android.support.annotation.Nullable;
import com.ayondo.academy.fragments.TestKChartsFragment;
import com.ayondo.academy.fragments.TypographyExampleFragment;
import com.ayondo.academy.fragments.achievement.AchievementListTestingFragment;
import com.ayondo.academy.fragments.achievement.QuestListTestingFragment;
import com.ayondo.academy.fragments.level.XpTestingFragment;

public class AdminSettingsFragmentUtil
{
    @Nullable public static Class getAchievementListTestingFragmentClass()
    {
        return AchievementListTestingFragment.class;
    }

    @Nullable public static Class getQuestListTestingFragmentClass()
    {
        return QuestListTestingFragment.class;
    }

    @Nullable public static Class getXpTestingFragmentClass()
    {
        return XpTestingFragment.class;
    }

    @Nullable public static Class getTypographyExampleFragment()
    {
        return TypographyExampleFragment.class;
    }

    @Nullable public static Class getKChartExampleFragment()
    {
        return TestKChartsFragment.class;
    }
}
