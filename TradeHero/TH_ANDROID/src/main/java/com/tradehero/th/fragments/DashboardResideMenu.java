package com.tradehero.th.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.special.residemenu.ResideMenu;
import javax.inject.Inject;

public class DashboardResideMenu extends ResideMenu
    implements DashboardNavigator.DashboardFragmentWatcher
{
    @Inject public DashboardResideMenu(Context context)
    {
        super(context);
    }

    @Override public <T extends Fragment> void onFragmentChanged(FragmentActivity fragmentActivity, Class<T> fragmentClass, Bundle bundle)
    {
        closeMenu();
    }
}
