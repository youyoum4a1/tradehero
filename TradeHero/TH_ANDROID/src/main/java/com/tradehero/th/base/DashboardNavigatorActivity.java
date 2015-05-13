package com.tradehero.th.base;

import android.support.v7.widget.Toolbar;

import com.tradehero.th.fragments.DashboardNavigator;

public interface DashboardNavigatorActivity
{
    DashboardNavigator getDashboardNavigator();
    Toolbar getToolbar();
}
