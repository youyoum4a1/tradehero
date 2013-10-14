package com.tradehero.th.activities;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.R;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;

public class DashboardActivity extends SherlockFragmentActivity
    implements NavigatorActivity
{
    public static final String TAG = DashboardActivity.class.getSimpleName();

    private Navigator navigator;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dashboard_with_bottom_bar);
        navigator = new DashboardNavigator(this, getSupportFragmentManager(), R.id.realtabcontent);
    }

    @Override public void onBackPressed()
    {
        //super.onBackPressed();
        navigator.popFragment();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        // required for fragment onOptionItemSelected to be called
        return super.onOptionsItemSelected(item);
    }

    //<editor-fold desc="NavigatorActivity">
    @Override public Navigator getNavigator()
    {
        return navigator;
    }
    //</editor-fold>
}
