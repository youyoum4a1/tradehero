package com.tradehero.th.fragments.updatecenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

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
        View view = inflater.inflate(R.layout.update_center, container, false);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        addTabs();
        changeTabTitleNumber(0, 80);
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
            //Fragment fragment = Fragment.instantiate(getActivity(), tabTitle.tabClass.getName(), args);
            //fragment.setOnFollowersLoadedListener(onFollowersLoadedListener);
            //Action Bar Tab must have a Callback
            ActionBar.Tab tab = actionBar.newTab().setTabListener(
                    new MyTabListener(getSherlockActivity(),tabTitle.tabClass,tabTitle.name(),args));
            tab.setTag(tabTitle.id);
            setTabStyle(tab);
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
        String title ;
        title = String.format(getSherlockActivity().getString(titleRes), number);
        TitleTabView tabView =  (TitleTabView)tab.getCustomView();
        tabView.setTitle(title);

        tabView.setTitleNumber(number);
    }

    private void changeTabTitleNumber(int page,int number)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        ActionBar.Tab tab = actionBar.getTabAt(page);
        TitleTabView tabView = (TitleTabView)tab.getCustomView();
        tabView.setTitleNumber(number);
    }

    private void setTabStyle(ActionBar.Tab tab)
    {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.message_tab_item, (ViewGroup)getActivity().getWindow().getDecorView(),false);

        tab.setCustomView(v);
        TitleTabView tabView =  (TitleTabView)tab.getCustomView();
    }

    public static interface TitleNumberCallback
    {
        void onTitleNumberChanged(int page, int number);
    }

    class MyTabListener extends TabListener
    {
        public MyTabListener(SherlockFragmentActivity activity,
                Class<? extends Fragment> fragmentClass, String tag, Bundle args)
        {
            super(activity, fragmentClass, tag, args);
        }

        @Override public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
        {
            if (mFragment == null)
            {
                mFragment = Fragment.instantiate(mActivity, mFragmentClass.getName(), mArgs);
                ft.add(R.id.fragment_content, mFragment, mTag);
            }
            else
            {
                ft.attach(mFragment);
            }
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
