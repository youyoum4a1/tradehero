package com.tradehero.th.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import com.tradehero.th.fragments.CommunityScreenFragment;
import com.tradehero.th.fragments.MeTimelineFragment;
import com.tradehero.th.fragments.PortfolioScreenFragment;
import com.tradehero.th.fragments.StoreScreenFragment;
import com.tradehero.th.fragments.trending.TrendingContainerFragment;
import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends SherlockFragmentActivity
{
    private static final String BUNDLE_KEY = "key";
    private FragmentTabHost mTabHost;
    private Fragment currentFragment;
    private Class<?> currentFragmentClass;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_with_bottom_bar);

        initiateViews();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    private void initiateViews()
    {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        addNewTab(getString(R.string.trending), R.drawable.trending_selector, TrendingContainerFragment.class);
        addNewTab(getString(R.string.community), R.drawable.community_selector, CommunityScreenFragment.class);
        addNewTab(getString(R.string.home), R.drawable.home_selector, MeTimelineFragment.class);
        addNewTab(getString(R.string.portfolio), R.drawable.pofilio_selector, PortfolioScreenFragment.class);
        addNewTab(getString(R.string.store), R.drawable.store_selector, StoreScreenFragment.class);

        mTabHost.setCurrentTabByTag(getString(R.string.home));
    }

    private void addNewTab(String tabTag, int tabIndicatorDrawableId, Class<?> fragmentClass)
    {
        Bundle b = new Bundle();
        b.putString(BUNDLE_KEY, tabTag);
        mTabHost.addTab(mTabHost
                .newTabSpec(tabTag)
                .setIndicator("", getResources().getDrawable(tabIndicatorDrawableId)),
                fragmentClass, b);
    }

    public void showTabs(boolean value)
    {
        if (mTabHost != null)
        {
            mTabHost.getTabWidget().setVisibility(value ? View.VISIBLE : View.GONE);
        }
    }

    @Override public void onAttachFragment(Fragment fragment)
    {
        super.onAttachFragment(fragment);
        currentFragmentClass = fragment.getClass();
        currentFragment = fragment;
    }

    public void conditionalSetCurrentFragmentByClass(Class<?> fragmentClass)
    {
        if (!currentFragmentClass.equals(fragmentClass))
        {
            setCurrentFragmentByClass(fragmentClass);
        }
    }

    private void setCurrentFragmentByClass(Class<?> fragmentClass)
    {
        currentFragment = FragmentFactory.getInstance(fragmentClass);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_right_in, R.anim.slide_left_out,
                        R.anim.slide_left_in, R.anim.slide_right_out)
                .replace(R.id.realtabcontent, currentFragment)
                .addToBackStack(null)
                .commit();
    }

    public Fragment getCurrentFragment()
    {
        return currentFragment;
    }

    private static class FragmentFactory
    {
        private static Map<Class<?>, Fragment> instances = new HashMap<>();

        public static Fragment getInstance(Class<?> clss)
        {
            Fragment fragment = instances.get(clss);
            if (fragment == null)
            {
                fragment = Fragment.instantiate(Application.context(), clss.getName(), null);
                instances.put(clss, fragment);
            }
            return fragment;
        }
    }
}
