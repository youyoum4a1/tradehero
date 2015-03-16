package com.tradehero.th.fragments.alert;

import dagger.Module;

@Module(
        injects = {
                AlertManagerFragment.class,
                AlertEditFragment.class,
                AlertCreateFragment.class,
                AlertListItemAdapter.class,
                AlertItemView.class,
                AlertSecurityProfile.class,
                AlertSliderView.class,
                AlertViewFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentAlertModule
{
}
