package com.tradehero.th.fragments.games;

import dagger.Module;

@Module(
        injects = {
                GameWebViewFragment.class,
                HowToPlayDialogFragment.class,
                MiniGameScoreDialogFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentGamesModule
{
}
