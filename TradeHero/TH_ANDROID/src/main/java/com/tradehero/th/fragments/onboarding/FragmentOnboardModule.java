package com.tradehero.th.fragments.onboarding;

import android.content.IntentFilter;
import com.tradehero.th.fragments.onboarding.hero.FragmentOnBoardHeroModule;
import com.tradehero.th.fragments.onboarding.pref.FragmentOnBoardPrefModule;
import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                FragmentOnBoardPrefModule.class,
                FragmentOnBoardHeroModule.class
        },
        injects = {
                OnBoardDialogFragment.class
        },
        library = true,
        complete = false
)
public class FragmentOnBoardModule
{
    public static final String ON_BOARD_INTENT_ACTION_NAME = "com.tradehero.th.onboard.ALERT";
    public static final String KEY_ON_BOARD_BROADCAST = FragmentOnBoardModule.class.getName()+".onboardBroadcast";

    @Provides @ForOnBoard IntentFilter providesIntentFilterOnBoard()
    {
        return new IntentFilter(ON_BOARD_INTENT_ACTION_NAME);
    }
}
