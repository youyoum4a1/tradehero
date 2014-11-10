package com.tradehero.th.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;

import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SingleAttributeEvent;
import com.tradehero.th.widget.THTabView;

import java.util.Collection;

public class DashboardTabHost extends TabHost
    implements DashboardNavigator.DashboardFragmentWatcher
{
    private final Collection<RootFragmentType> bottomBarFragmentTypes = RootFragmentType.forBottomBar();
    private final Collection<RootFragmentType> slideFragmentTypes = RootFragmentType.forResideMenu();
    private Animation slideInAnimation;
    private Animation slideOutAnimation;
    private OnTranslateListener onTranslateListener;
    private Analytics analytics;

    public DashboardTabHost(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setAnalytics(Analytics analytics) {
        this.analytics = analytics;
    }

    @Override public void setup()
    {
        super.setup();

        // slide in and out animation
        slideInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in);
        slideOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out);

        // Populate tabs for the button bar
        for (RootFragmentType tabType: bottomBarFragmentTypes)
        {
            addNewTab(tabType);
        }
    }
    private TabHost.TabSpec makeTabSpec(RootFragmentType tabType)
    {
        return newTabSpec(tabType.toString())
                .setContent(new DummyTabContentFactory());
    }

    private void addNewTab(RootFragmentType tabType)
    {
        THTabView indicator = THTabView.inflateWith(getTabWidget());
        indicator.setIcon(tabType.drawableResId);
        addTab(makeTabSpec(tabType)
                .setIndicator(indicator));
    }

    @Override public <T extends Fragment> void onFragmentChanged(FragmentActivity fragmentActivity, Class<T> fragmentClass, Bundle bundle)
    {
        showTabBar();
        reportAnalytics(fragmentClass);
        for (RootFragmentType rootFragmentType: bottomBarFragmentTypes)
        {
            if (rootFragmentType.fragmentClass == fragmentClass)
            {
                setCurrentTabByTag(rootFragmentType.toString());
                return;
            }
        }
        // none of the bottom bar fragment, hide me
        //hideTabBar();
    }

    private <T extends Fragment> void reportAnalytics(Class<T> fragmentClass)
    {
        if (RootFragmentType.values()[getCurrentTab()].fragmentClass == fragmentClass)
        {
            analytics.fireEvent(new SingleAttributeEvent(
                    RootFragmentType.values()[getCurrentTab()].analyticsString,
                    AnalyticsConstants.ClickedFrom, AnalyticsConstants.Bottom));
        }
        else
        {
            for (RootFragmentType rootFragmentType: slideFragmentTypes)
            {
                if (rootFragmentType.fragmentClass == fragmentClass)
                {
                    analytics.fireEvent(new SingleAttributeEvent(rootFragmentType.analyticsString,
                            AnalyticsConstants.ClickedFrom, AnalyticsConstants.Side));
                    return;
                }
            }

        }
    }

    private void hideTabBar()
    {
        if (getVisibility() != View.GONE)
        {
            startAnimation(slideOutAnimation);
            setVisibility(View.GONE);
        }
    }

    private void showTabBar()
    {
        if (getVisibility() != View.VISIBLE || getTranslationY() > 0.0)
        {
            setVisibility(View.VISIBLE);
            //startAnimation(slideInAnimation);
            // TODO this is a HACK to workaround problem with QuickReturnListViewOnScrollListener class, that class need to be improved
            setTranslationY(0);
        }
    }

    public void animateHide()
    {
        animate().translationYBy(getResources().getDimensionPixelSize(R.dimen.dashboard_tabhost_height)).start();
    }

    public void animateShow()
    {
        animate().translationY(0).start();
    }

    @Override public void setTranslationY(float translationY)
    {
        if (onTranslateListener != null)
        {
            onTranslateListener.onTranslate(0, translationY);
        }
        super.setTranslationY(translationY);
    }

    public void setOnTranslate(OnTranslateListener onTranslateListener)
    {
        this.onTranslateListener = onTranslateListener;
    }

    private class DummyTabContentFactory implements TabContentFactory
    {
        @Override public View createTabContent(String tag)
        {
            return new View(getContext());
        }
    }

    public static interface OnTranslateListener
    {
        void onTranslate(float x, float y);
    }
}
