package com.tradehero.th.fragments.onboarding;

import android.content.IntentFilter;
import com.tradehero.th.fragments.competition.ForCompetitionEnrollment;
import com.tradehero.th.fragments.onboarding.hero.FragmentOnBoardHeroComponent;
import dagger.Component;
import dagger.Provides;

@Component(modules = { FragmentOnBoardComponent.Module.class })
public interface FragmentOnBoardComponent extends FragmentOnBoardHeroComponent
{
    void injectOnBoardDialogFragment(OnBoardDialogFragment target);

    @dagger.Module
    static class Module
    {
        public static final String ON_BOARD_INTENT_ACTION_NAME = "com.tradehero.th.onboard.ALERT";
        public static final String ENROLLMENT_INTENT_ACTION_NAME = "com.tradehero.th.enrollment.ALERT";

        @Provides @ForOnBoard IntentFilter providesIntentFilterOnBoard()
        {
            return new IntentFilter(ON_BOARD_INTENT_ACTION_NAME);
        }

        @Provides @ForCompetitionEnrollment IntentFilter providesIntentFilterEnrollment()
        {
            return new IntentFilter(ENROLLMENT_INTENT_ACTION_NAME);
        }
    }
}