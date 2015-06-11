package com.tradehero.chinabuild.fragment.security;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.utils.DaggerUtils;

import javax.inject.Inject;

/**
 * Created by palmer on 15/6/10.
 */
public class SecurityDetailSubDiscussFragment extends Fragment {

    @Inject Analytics analytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security_detail_discuss, container, false);
        return view;
    }


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
