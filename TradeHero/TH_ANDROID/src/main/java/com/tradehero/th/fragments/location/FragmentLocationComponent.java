package com.tradehero.th.fragments.location;

import dagger.Component;

/**
 * Created by tho on 9/9/2014.
 */
@Component
public interface FragmentLocationComponent
{
    void injectLocationListFragment(LocationListFragment target);
}
