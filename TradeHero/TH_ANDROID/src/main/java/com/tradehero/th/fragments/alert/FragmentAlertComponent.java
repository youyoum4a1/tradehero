package com.tradehero.th.fragments.alert;

import dagger.Component;

@Component
public interface FragmentAlertComponent
{
    void injectAlertManagerFragment(AlertManagerFragment alertManagerFragment);
    void injectAlertEditFragment(AlertEditFragment alertEditFragment);
    void injectAlertCreateFragment(AlertCreateFragment alertCreateFragment);
    void injectAlertListItemAdapter(AlertListItemAdapter alertListItemAdapter);
    void injectAlertItemView(AlertItemView alertItemView);
    void injectAlertViewFragment(AlertViewFragment alertViewFragment);
}
