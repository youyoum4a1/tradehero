package com.tradehero.th.fragments.web;

import dagger.Module;

@Module(
        injects = {
                THWebViewClientTest.class,
                WebViewFragmentTest.class,
        },
        library = true,
        complete = false
)
public class FragmentWebTestModule
{
}
