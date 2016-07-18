package com.androidth.general.fragments.settings;

/**
 * Created by jeffgan on 18/7/16.
 */
import android.support.annotation.Nullable;
import com.androidth.general.fragments.TestKChartsFragment;
import com.androidth.general.fragments.TypographyExampleFragment;
import com.androidth.general.fragments.achievement.AchievementListTestingFragment;
import com.androidth.general.fragments.achievement.QuestListTestingFragment;
import com.androidth.general.fragments.level.XpTestingFragment;

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