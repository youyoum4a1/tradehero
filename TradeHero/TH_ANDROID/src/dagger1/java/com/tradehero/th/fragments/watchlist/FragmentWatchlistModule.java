package com.ayondo.academy.fragments.watchlist;

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
