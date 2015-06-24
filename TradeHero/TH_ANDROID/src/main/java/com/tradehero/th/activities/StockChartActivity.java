package com.tradehero.th.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.tradehero.th.R;
import com.tradehero.th.base.THApp;
import com.tradehero.th.fragments.security.ChartFragment;
import com.tradehero.th.inject.Injector;

public class StockChartActivity extends FragmentActivity
        implements Injector, AchievementAcceptor
{
    private Injector newInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        THApp app = THApp.get(this);
        newInjector = app.plus(new StockChartActivityModule());

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stock_chart);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ChartFragment fragment = new ChartFragment();
        fragment.setArguments(getIntent().getExtras().getBundle(ChartFragment.BUNDLE_KEY_ARGUMENTS));
        fragmentTransaction.add(R.id.stock_chart, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override public void inject(Object o)
    {
        newInjector.inject(o);
    }
}
