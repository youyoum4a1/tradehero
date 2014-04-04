package com.tradehero.th.fragments.updatecenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import java.text.MessageFormat;

/**
 * Created by thonguyen on 3/4/14.
 */
public class UpdateCenterFragment extends DashboardFragment
{
    public static final String KEY_PAGE = "page";

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store_manage_heroes_2, container, false);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        addTabs();
    }

    @Override public void onDestroyView()
    {
        clearTabs();

        super.onDestroyView();
    }

    private void addTabs()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        Bundle args = getArguments();
        if (args == null)
        {
            args = new Bundle();
        }

        UpdateCenterTabType[] types = UpdateCenterTabType.values();
        for (UpdateCenterTabType tabTitle : types)
        {
            args.putInt(KEY_PAGE, tabTitle.pageIndex);
            Fragment fragment = Fragment.instantiate(getActivity(), tabTitle.tabClass.getName(), args);
            
            ActionBar.Tab tab = actionBar.newTab().setTabListener(new TabListener(fragment));
            tab.setTag(tabTitle.id);
            setTabTitleNumber(tab, tabTitle.titleRes, 0);
            actionBar.addTab(tab);
        }
    }

    private void clearTabs()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.removeAllTabs();
    }

    private void setTabTitleNumber(ActionBar.Tab tab, int titleRes, int number)
    {
        String title = "";
        title = MessageFormat.format(getSherlockActivity().getString(titleRes), number);
        tab.setText(title);
    }

    private void setTabStyle(ActionBar.Tab tab)
    {
        tab.setCustomView(R.layout.message_tab_item);
        TitleTabView tabView =  (TitleTabView)tab.getCustomView();
    }

    public static interface TitleNumberCallback
    {
        void onTitleNumberChanged(int page, int number);
    }

    /**
     * Callback
     */
    private class TabListener implements ActionBar.TabListener
    {

        private Fragment mFragment;

        public TabListener(Fragment fragment)
        {
            mFragment = fragment;
        }

        @Override public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
        {
            ft.add(R.id.fragment_content, mFragment, mFragment.getTag());
        }

        @Override public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
        {
            ft.remove(mFragment);
        }

        @Override public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
        {
            //Toast.makeText(ActionBarTabs.this, "Reselected!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
