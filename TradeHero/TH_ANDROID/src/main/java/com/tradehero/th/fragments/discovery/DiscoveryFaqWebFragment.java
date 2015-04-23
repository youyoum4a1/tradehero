package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.fragments.web.WebViewFragment;
import javax.inject.Inject;

public class DiscoveryFaqWebFragment extends WebViewFragment
{
    @SuppressWarnings("unused") @Inject Context doNotRemoveOrItFails;

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

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.settings_primary_faq);
    }
}
