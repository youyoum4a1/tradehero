package com.tradehero.th.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tradehero.thm.R;
import com.tradehero.th.fragments.security.ChartFragment;

public class StockChartActivity extends SherlockFragmentActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_chart);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ChartFragment fragment = new ChartFragment();
        fragment.setArguments(getIntent().getExtras().getBundle(ChartFragment.BUNDLE_KEY_ARGUMENTS));
        fragmentTransaction.add(R.id.stock_chart, fragment);
        fragmentTransaction.commit();
    }
}
