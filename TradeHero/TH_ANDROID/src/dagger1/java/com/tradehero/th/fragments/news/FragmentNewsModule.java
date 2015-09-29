package com.tradehero.th.fragments.news;

import dagger.Module;

@Module(
        injects = {
                NewsHeadlineFragment.class,
                SecurityNewsfeedFragment.class,
                ShareDialogLayout.class,
                NewsDialogLayout.class,
                NewsHeadlineViewLinear.class,
                NewsViewLinear.class,
                NewsItemCompactViewHolder.class,
                NewsItemViewHolder.class,
                NewsWebFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentNewsModule
{
}
