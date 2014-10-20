package com.tradehero.th.fragments.discovery;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.discussion.DiscussionEditPostFragment;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class DiscoveryMainFragment extends DashboardFragment
        implements ActionBar.TabListener
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;
    private DiscoveryPagerAdapter discoveryPagerAdapter;
    @InjectView(R.id.pager) ViewPager tabViewPager;
    private MenuItem postMenuButton;
    private static int selectedTab = 0;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        discoveryPagerAdapter = new DiscoveryPagerAdapter(this.getChildFragmentManager());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_main_fragment, container, false);
        ButterKnife.inject(this, view);
        tabViewPager.setAdapter(discoveryPagerAdapter);
        return view;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(getString(R.string.discovery));
        inflater.inflate(R.menu.menu_create_post_discussion, menu);
        postMenuButton = menu.findItem(R.id.discussion_edit_post);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            setupTabs(actionBar);
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.discussion_edit_post)
        {
            navigator.get().pushFragment(DiscussionEditPostFragment.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroyOptionsMenu()
    {
        this.postMenuButton = null;
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        tabViewPager.setAdapter(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.discoveryPagerAdapter = null;
        super.onDestroy();
    }

    private void setupTabs(@NotNull ActionBar actionBar)
    {
        DiscoveryTabType[] types = DiscoveryTabType.values();
        int savedSelectedTab = selectedTab;
        if (actionBar.getTabCount() != types.length)
        {
            actionBar.removeAllTabs();
            for (DiscoveryTabType type : types)
            {
                actionBar.addTab(
                        actionBar.newTab()
                                .setText(type.titleStringResId)
                                .setTabListener(this)
                                .setTag(type));
            }
        }
        else
        {
            for (int i = 0; i < actionBar.getTabCount(); i++)
            {
                actionBar.getTabAt(i).setTabListener(this);
            }
        }
        actionBar.setSelectedNavigationItem(savedSelectedTab);
        setPagerAt();
    }

    private class DiscoveryPagerAdapter extends FragmentPagerAdapter
    {
        public DiscoveryPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
        }

        @Override public Fragment getItem(int position)
        {
            DiscoveryTabType tabType = DiscoveryTabType.values()[position];
            Bundle args = new Bundle();
            ActionBarOwnerMixin.putKeyShowHomeAsUp(args, false);
            return Fragment.instantiate(getActivity(), tabType.fragmentClass.getName(), args);
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(DiscoveryTabType.values()[position].titleStringResId);
        }

        @Override public int getCount()
        {
            return DiscoveryTabType.values().length;
        }
    }

    //region TabListener
    @Override public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        selectedTab = tab.getPosition();
        setPagerAt();

        MenuItem postMenuButtonCopy = postMenuButton;
        if (postMenuButtonCopy != null)
        {
            postMenuButtonCopy.setVisible(((DiscoveryTabType) tab.getTag()).showComment);
        }
    }

    @Override public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
    {
    }

    @Override public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
    {
    }
    //endregion

    protected void setPagerAt()
    {
        ViewPager pager = tabViewPager;
        if (pager != null && selectedTab != tabViewPager.getCurrentItem())
        {
            tabViewPager.setCurrentItem(selectedTab);
        }
    }
}
