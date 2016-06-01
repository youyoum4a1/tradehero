package com.ayondo.academy.fragments.alert;

import dagger.Module;

@Module(
        injects = {
                AlertManagerFragment.class,
                AlertCreateDialogFragment.class,
                AlertEditDialogFragment.class,
                AlertListItemAdapter.class,
                AlertItemView.class,
                AlertSliderView.class,
        },
        library = true,
        complete = false
)
public class FragmentAlertModule
{
}
