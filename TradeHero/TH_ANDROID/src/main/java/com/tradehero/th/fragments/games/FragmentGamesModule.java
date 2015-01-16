package com.tradehero.th.fragments.games;

import dagger.Module;

@Module(
        injects = {
                GameWebViewFragment.class,
                HowToPlayDialogFragment.class,
                MiniGameScoreDialogFragment.class,
                ViralGamePopupDialogFragment.class,
                ViralGameWebFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentGamesModule
{
}
