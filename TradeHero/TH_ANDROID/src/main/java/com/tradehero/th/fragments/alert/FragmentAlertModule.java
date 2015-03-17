package com.tradehero.th.fragments.alert;

import dagger.Module;

@Module(
        injects = {
                AlertManagerFragment.class,
                AlertCreateDialogFragment.class,
                AlertCreateFragment.class,
                AlertEditDialogFragment.class,
                AlertEditFragment.class,
                AlertListItemAdapter.class,
                AlertItemView.class,
                AlertSecurityProfile.class,
                AlertSliderView.class,
        },
        library = true,
        complete = false
)
public class FragmentAlertModule
{
}
