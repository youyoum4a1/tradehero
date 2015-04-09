package com.tradehero.th.fragments.web;

import dagger.Module;

@Module(
        injects = {
                BaseWebViewFragment.class,
                THWebViewClient.class,
                WebViewFragment.class,
                XWalkWebViewFragment.class
        },
        library = true,
        complete = false
)
public class FragmentWebModule
{
}
