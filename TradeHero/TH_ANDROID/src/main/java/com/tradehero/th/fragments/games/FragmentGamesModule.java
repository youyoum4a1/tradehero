package com.tradehero.th.fragments.games;

import com.tradehero.th.fragments.games.popquiz.PopQuizGameModule;
import dagger.Module;

@Module(
        injects = {
                GameWebViewFragment.class,
                HowToPlayDialogFragment.class,
                MiniGameScoreDialogFragment.class,
                ViralGamePopupDialogFragment.class,
                ViralGameWebFragment.class,
        },
        includes = {
                PopQuizGameModule.class
        },
        library = true,
        complete = false
)
public class FragmentGamesModule
{
}
