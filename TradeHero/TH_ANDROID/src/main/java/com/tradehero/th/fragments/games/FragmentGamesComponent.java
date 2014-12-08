package com.tradehero.th.fragments.games;

import dagger.Component;

@Component
public interface FragmentGamesComponent
{
    void injectGameWebViewFragment(GameWebViewFragment target);
    void injectHowToPlayDialogFragment(HowToPlayDialogFragment target);
    void injectMiniGameScoreDialogFragment(MiniGameScoreDialogFragment target);
}
