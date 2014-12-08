package com.tradehero.th.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.tradehero.th.BottomTabsQuickReturnScrollViewListener;
import com.tradehero.th.R;
import com.tradehero.th.UIModule;
import com.tradehero.th.THApp;
import com.tradehero.th.fragments.security.ChartFragment;
import com.tradehero.th.inject.Injector;
import dagger.Module;
import dagger.Provides;

public class StockChartActivity extends FragmentActivity
        implements Injector
{
    private Injector newInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        THApp app = THApp.get(this);
        // FIXME dagger2
        //newInjector = app.plus(new StockChartActivityModule());

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stock_chart);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ChartFragment fragment = new ChartFragment();
        fragment.setArguments(getIntent().getExtras().getBundle(ChartFragment.BUNDLE_KEY_ARGUMENTS));
        fragmentTransaction.add(R.id.stock_chart, fragment);
        fragmentTransaction.commit();
    }

    @Override public void inject(Object o)
    {
        newInjector.inject(o);
    }

    @Module(
            includes = {
                    UIModule.class
            },
            library = true,
            complete = false
    )
    public class StockChartActivityModule
    {
        @Provides @BottomTabsQuickReturnScrollViewListener NotifyingScrollView.OnScrollChangedListener provideQuickReturnListViewOnScrollListener()
        {
            return null;
        }
    }
}
