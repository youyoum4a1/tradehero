package com.tradehero.th.fragments.games;

import dagger.Module;

@Module(
        injects = {
                GameWebViewFragment.class,
                HowToPlayDialogFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentGamesModule
{
}
