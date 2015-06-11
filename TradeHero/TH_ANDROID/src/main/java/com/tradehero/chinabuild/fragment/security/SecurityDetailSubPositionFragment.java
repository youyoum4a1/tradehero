package com.tradehero.chinabuild.fragment.security;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.utils.DaggerUtils;

import javax.inject.Inject;

/**
 * Created by palmer on 15/6/10.
 */
public class SecurityDetailSubPositionFragment extends Fragment implements View.OnClickListener{

    @Inject Analytics analytics;
    private ImageView emptyIV;
    private LinearLayout optsLL;
    private TextView moreTV;

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
        View view = inflater.inflate(R.layout.fragment_security_detail_position, container, false);
        emptyIV = (ImageView)view.findViewById(R.id.imageview_sub_position_empty);
        optsLL = (LinearLayout)view.findViewById(R.id.linearlayout_positions);
        moreTV = (TextView)view.findViewById(R.id.textview_more);
        moreTV.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId){
            case R.id.textview_more:
                enterUserPositionsPage();
                break;
        }
    }

    private void enterUserPositionsPage(){
        Bundle bundle = new Bundle();
        pushFragment(SecurityUserPositionFragment.class, bundle);
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
