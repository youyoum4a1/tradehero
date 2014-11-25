package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.fragments.web.WebViewFragment;
import javax.inject.Inject;

public class DiscoveryFaqWebFragment extends WebViewFragment
{
    @Inject Context doNotRemove;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args == null)
        {
            args = new Bundle();
        }
        putUrl(args, getString(R.string.th_faq_url));
    }
}
