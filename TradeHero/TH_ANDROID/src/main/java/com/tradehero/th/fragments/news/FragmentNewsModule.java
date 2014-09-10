package com.tradehero.th.fragments.news;

import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
@Module(
        injects = {
                NewsHeadlineFragment.class,
                ShareDialogLayout.class,
                NewsDialogLayout.class,
                NewsHeadlineViewLinear.class,
                NewsViewLinear.class,
                NewsItemCompactViewHolder.class,
                NewsItemViewHolder.class,

                // TODO check
                ShareDestinationSetAdapter.class
        },
        library = true,
        complete = false
)
public class FragmentNewsModule
{
}
