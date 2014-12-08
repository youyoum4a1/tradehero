package com.tradehero.th.fragments.web;

import dagger.Component;

@Component
public interface FragmentWebComponent
{
    void injectBaseWebViewFragment(BaseWebViewFragment target);
    void injectTHWebViewClient(THWebViewClient target);
    void injectWebViewFragment(WebViewFragment target);
}
