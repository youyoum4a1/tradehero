package com.tradehero.th.fragments.contestcenter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.GraphicUtil;
import com.tradehero.th.widget.THTabView;
import javax.inject.Inject;

public class ContestCenterFragment extends DashboardFragment
{
    @Inject GraphicUtil graphicUtil;

    private static final int FRAGMENT_LAYOUT_ID = 10001;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.dashboard_contest_center);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return addTabs();
    }

    private View addTabs()
    {
        FragmentTabHost mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), this.getChildFragmentManager(), FRAGMENT_LAYOUT_ID);
        graphicUtil.setBackground(mTabHost.getTabWidget(), Color.WHITE);
        Bundle args = getArguments();
        if (args == null)
        {
            args = new Bundle();
        }
        ContestCenterTabType[] types = ContestCenterTabType.values();
        for (ContestCenterTabType tabTitle : types)
        {
            args = new Bundle(args);
            THTabView tabView = THTabView.inflateWith(mTabHost.getTabWidget());
            String title = getString(tabTitle.titleRes, 0);
            tabView.setTitle(title);
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(title).setIndicator(tabView);
            mTabHost.addTab(tabSpec, tabTitle.tabClass, args);
        }

        return mTabHost;
    }
}
