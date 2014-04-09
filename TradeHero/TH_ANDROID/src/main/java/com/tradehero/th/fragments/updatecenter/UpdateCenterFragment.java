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
import com.tradehero.th.fragments.base.BaseFragment;
import java.util.Arrays;
import timber.log.Timber;

/**
 * Created by thonguyen on 3/4/14.
 */
public class UpdateCenterFragment extends BaseFragment /*DashboardFragment*/
{
    public static final String KEY_PAGE = "page";
    TitleNumberCallback titleNumberCallback;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        titleNumberCallback = new TabTitleNumberCallback();
        Timber.d("onCreate");
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
        //TODO
        changeTabTitleNumber(0, 80);
    }

    @Override public void onDestroyView()
    {
        clearTabs();

        super.onDestroyView();
        Timber.d("onDestroyView");
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        titleNumberCallback = null;
        Timber.d("onDestroy");
    }

    private void addTabs()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

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
            ActionBar.Tab tab = actionBar.newTab().setTabListener(
                    new MyTabListener(getSherlockActivity(), tabTitle.tabClass, tabTitle.tabClass+"_"+tabTitle.name(),
                            args));
            tab.setTag(tabTitle.id);
            //tab.setText(tabTitle.titleRes);
            setTabStyle(tab);
            setTabTitleNumber(tab, tabTitle.titleRes, 0);
            actionBar.addTab(tab);
        }
        Timber.d("addTabs %s", Arrays.toString(types));
    }

    private void clearTabs()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.removeAllTabs();
        Timber.d("clearTabs");
    }

    private void setTabTitleNumber(ActionBar.Tab tab, int titleRes, int number)
    {
        String title;
        title = String.format(getSherlockActivity().getString(titleRes), number);
        TitleTabView tabView = (TitleTabView) tab.getCustomView();
        tabView.setTitle(title);

        tabView.setTitleNumber(number);
    }

    private void changeTabTitleNumber(int page, int number)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        ActionBar.Tab tab = actionBar.getTabAt(page);
        TitleTabView tabView = (TitleTabView) tab.getCustomView();
        tabView.setTitleNumber(number);
    }

    private void setTabStyle(ActionBar.Tab tab)
    {
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.message_tab_item,
                        (ViewGroup) getActivity().getWindow().getDecorView(), false);

        tab.setCustomView(v);
        TitleTabView tabView = (TitleTabView) tab.getCustomView();
    }

    class TabTitleNumberCallback implements TitleNumberCallback {
        @Override public void onTitleNumberChanged(int page, int number)
        {
            changeTabTitleNumber(page, number);
        }
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

                Timber.d("onTabSelected add fragment %s %s", tab.getTag(),mFragment.getClass().getSimpleName());
            }
            else
            {
                ft.attach(mFragment);
                Timber.d("onTabSelected attach fragment %s %s", tab.getTag(),mFragment.getClass().getSimpleName());
            }
        }

        @Override public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
        {
        }
    }

    //@Override public boolean isTabBarVisible()
    //{
    //    return false;
    //}
}
