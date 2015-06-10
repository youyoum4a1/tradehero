package com.tradehero.chinabuild.fragment.security;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;


/**
 * Created by palmer on 15/6/10.
 */
public class SecurityDetailSubNewsFragment extends Fragment{
    private DashboardNavigator getDashboardNavigator() {
        DashboardNavigatorActivity activity = ((DashboardNavigatorActivity) getActivity());
        if (activity != null) {
            return activity.getDashboardNavigator();
        }
        return null;
    }

    private Fragment pushFragment(Class fragmentClass, Bundle args) {
        return getDashboardNavigator().pushFragment(fragmentClass, args);
    }
}
