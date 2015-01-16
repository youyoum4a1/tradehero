
package com.tradehero.th.fragments.games;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;

@Routable("facebookshare/")
public class ViralGameShareFragment extends BaseFragment
{
    @Inject THRouter thRouter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
        Bundle args = getArguments();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.webview_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
