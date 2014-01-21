package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

public class ReferralFragment extends DashboardFragment
{
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.refer_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {

    }

    //<editor-fold desc="Tab bar informer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}