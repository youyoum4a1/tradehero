package com.tradehero.th.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TabHost;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import java.util.Collection;

public class DashboardTabHost extends TabHost
    implements DashboardNavigator.DashboardFragmentWatcher
{
    private final Collection<RootFragmentType> bottomBarFragmentTypes = RootFragmentType.forBottomBar();

    public DashboardTabHost(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override public void setup()
    {
        super.setup();

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
                return;
            }
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
