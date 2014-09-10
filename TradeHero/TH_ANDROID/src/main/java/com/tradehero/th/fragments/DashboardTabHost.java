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

import com.tradehero.th.R;
import com.tradehero.th.fragments.dashboard.RootFragmentType;

import java.util.Collection;

public class DashboardTabHost extends TabHost
    implements DashboardNavigator.DashboardFragmentWatcher
{
    private final Collection<RootFragmentType> bottomBarFragmentTypes = RootFragmentType.forBottomBar();
    private Animation slideInAnimation;
    private Animation slideOutAnimation;

    public DashboardTabHost(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override public void setup()
    {
        super.setup();
        slideInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in);
        slideOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out);

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
        addTab(makeTabSpec(tabType)
                .setIndicator("", getResources().getDrawable(tabType.drawableResId)));
    }

    @Override public <T extends Fragment> void onFragmentChanged(FragmentActivity fragmentActivity, Class<T> fragmentClass, Bundle bundle)
    {
        for (RootFragmentType rootFragmentType: bottomBarFragmentTypes)
        {
            if (rootFragmentType.fragmentClass == fragmentClass)
            {
                setCurrentTabByTag(rootFragmentType.toString());
                //showTabBar();
                return;
            }
        }
        // none of the bottom bar fragment, hide me
        //hideTabBar();
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
        if (getVisibility() != View.VISIBLE)
        {
            setVisibility(View.VISIBLE);
            startAnimation(slideInAnimation);
        }
    }

    private class DummyTabContentFactory implements TabContentFactory
    {
        @Override public View createTabContent(String tag)
        {
            return new View(getContext());
        }
    }
}
