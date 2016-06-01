package com.ayondo.academy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.ayondo.academy.R;
import com.ayondo.academy.api.security.SecurityId;
import com.ayondo.academy.base.THApp;
import com.ayondo.academy.fragments.security.ChartFragment;
import com.ayondo.academy.inject.Injector;
import com.ayondo.academy.models.chart.ChartTimeSpan;

public class StockChartActivity extends FragmentActivity
        implements Injector, AchievementAcceptor
{
    private static final String BUNDLE_KEY_SECURITY_ID = StockChartActivity.class.getName() + "securityId";
    private final static String BUNDLE_KEY_TIME_SPAN_SECONDS_LONG = StockChartActivity.class.getName() + ".timeSpanSecondsLong";
    private final static String BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY = StockChartActivity.class.getName() + ".timeSpanButtonSetVisibility";

    //<editor-fold desc="Arguments Passing">
    public static void putSecurityId(@NonNull Intent intent, @NonNull SecurityId securityId)
    {
        intent.putExtra(BUNDLE_KEY_SECURITY_ID, securityId.getArgs());
    }

    @NonNull private static SecurityId getSecurityId(@NonNull Intent intent)
    {
        Bundle securityArgs = intent.getBundleExtra(BUNDLE_KEY_SECURITY_ID);
        if (securityArgs != null)
        {
            return new SecurityId(securityArgs);
        }
        throw new IllegalArgumentException("SecurityId cannot be null");
    }

    public static void putChartTimeSpan(@NonNull Intent intent, @NonNull ChartTimeSpan chartTimeSpan)
    {
        intent.putExtra(BUNDLE_KEY_TIME_SPAN_SECONDS_LONG, chartTimeSpan.duration);
    }

    @Nullable private static ChartTimeSpan getChartTimeSpan(@NonNull Intent intent)
    {
        if (intent.hasExtra(BUNDLE_KEY_TIME_SPAN_SECONDS_LONG))
        {
            return new ChartTimeSpan(intent.getLongExtra(BUNDLE_KEY_TIME_SPAN_SECONDS_LONG, 0));
        }
        return null;
    }

    public static void putButtonSetVisibility(@NonNull Intent intent, @ViewVisibilityValue int visibility)
    {
        intent.putExtra(BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY, visibility);
    }

    @ViewVisibilityValue public static int getButtonSetVisibility(@NonNull Intent intent, @ViewVisibilityValue int defaultValue)
    {
        int visibility = intent.getIntExtra(BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY, defaultValue);
        switch (visibility)
        {
            case View.VISIBLE:
                return View.VISIBLE;
            case View.INVISIBLE:
                return View.INVISIBLE;
            case View.GONE:
                return View.GONE;
        }
        throw new IllegalArgumentException("Visibility " + visibility + " is not a valid value");
    }
    //</editor-fold>

    private Injector newInjector;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        THApp app = THApp.get(this);
        newInjector = app.plus(new StockChartActivityModule());

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stock_chart);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        ChartFragment.putSecurityId(args, getSecurityId(getIntent()));
        ChartTimeSpan timeSpan = getChartTimeSpan(getIntent());
        if (timeSpan != null)
        {
            ChartFragment.putChartTimeSpan(args, timeSpan);
        }
        ChartFragment.putButtonSetVisibility(args, getButtonSetVisibility(getIntent(), View.VISIBLE));
        fragment.setArguments(args);
        fragmentTransaction.add(R.id.stock_chart, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override public void inject(Object o)
    {
        newInjector.inject(o);
    }
}
