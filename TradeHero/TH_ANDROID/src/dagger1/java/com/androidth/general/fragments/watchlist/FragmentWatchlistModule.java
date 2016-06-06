package com.androidth.general.fragments.watchlist;

import dagger.Module;

@Module(
        injects = {
                WatchlistItemView.class,
                MainWatchlistPositionFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentWatchlistModule
{
}
