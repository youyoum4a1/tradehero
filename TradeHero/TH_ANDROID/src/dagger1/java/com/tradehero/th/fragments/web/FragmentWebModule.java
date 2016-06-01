package com.ayondo.academy.fragments.web;

import dagger.Module;

@Module(
        injects = {
                BaseWebViewFragment.class,
                BaseWebViewIntentFragment.class,
                THWebViewClient.class,
                THWebViewIntentClient.class,
                WebViewFragment.class,
                WebViewIntentFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentWebModule
{
}
