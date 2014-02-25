package com.tradehero.th.fragments.competition;

import android.content.Intent;
import android.net.Uri;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.R;
import com.tradehero.th.fragments.web.BaseWebViewFragment;

/**
 * Created by xavier on 2/25/14.
 */
public class CompetitionWebViewFragment extends BaseWebViewFragment
{
    @Override protected int getLayoutResId()
    {
        return R.layout.fragment_webview;
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.competition_webview_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.webview_done:
                getNavigator().popFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

}
