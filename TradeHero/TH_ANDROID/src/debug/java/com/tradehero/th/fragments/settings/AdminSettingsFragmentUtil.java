package com.tradehero.th.fragments.settings;

import android.support.annotation.Nullable;
import com.tradehero.th.fragments.TestKChartsFragment;
import com.tradehero.th.fragments.LiveDevSettingFragment;
import com.tradehero.th.fragments.TypographyExampleFragment;
import com.tradehero.th.fragments.achievement.AchievementListTestingFragment;
import com.tradehero.th.fragments.achievement.QuestListTestingFragment;
import com.tradehero.th.fragments.level.XpTestingFragment;

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

    @Nullable public static Class getLiveDevSettingFragment() { return LiveDevSettingFragment.class; }
}
