package com.tradehero.th.fragments.alert;

import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
@Module(
        injects = {
                AlertManagerFragment.class,
                AlertEditFragment.class,
                AlertCreateFragment.class,

                AlertListItemAdapter.class,
                AlertItemView.class,
                AlertViewFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentAlertModule
{
}
