package com.tradehero.th.fragments.web;

import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
@Module(
        injects = {
                WebViewFragment.class,
                THWebViewClient.class
        },
        library = true,
        complete = false
)
public class FragmentWebModule
{
}
