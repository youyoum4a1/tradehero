package com.tradehero.th.fragments.home;

import dagger.Component;

@Component
public interface FragmentHomeComponent
{
    void injectHomeFragment(HomeFragment target);
    void injectHomeWebView(HomeWebView target);
}
