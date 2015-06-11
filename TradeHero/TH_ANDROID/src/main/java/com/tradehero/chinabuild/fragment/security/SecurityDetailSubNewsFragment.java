package com.tradehero.chinabuild.fragment.security;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.utils.DaggerUtils;

import javax.inject.Inject;


/**
 * Created by palmer on 15/6/10.
 */
public class SecurityDetailSubNewsFragment extends Fragment{

    private String securityName;
    private SecurityId securityId;

    @Inject Analytics analytics;
    private LinearLayout newsLL;
    private ImageView emptyIV;

    //Layout 0
    private RelativeLayout rl0;
    private TextView newsTV0;
    private TextView dateTV0;

    //Layout 1
    private RelativeLayout rl1;
    private TextView newsTV1;
    private TextView dateTV1;
    private View seperateLine1;

    //Layout 2
    private RelativeLayout rl2;
    private TextView newsTV2;
    private TextView dateTV2;
    private View seperateLine2;

    //Layout 3
    private RelativeLayout rl3;
    private TextView newsTV3;
    private TextView dateTV3;
    private View seperateLine3;

    //Layout 4
    private RelativeLayout rl4;
    private TextView newsTV4;
    private TextView dateTV4;
    private View seperateLine4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArguments();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    private void initArguments() {
        Bundle args = getArguments();
        Bundle securityIdBundle = args.getBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE);
        securityName = args.getString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME);
        if (securityIdBundle != null) {
            securityId = new SecurityId(securityIdBundle);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security_detail_news, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view){
        newsLL = (LinearLayout)view.findViewById(R.id.linearlayout_news);
        emptyIV = (ImageView)view.findViewById(R.id.imageview_empty);

        rl0 = (RelativeLayout)view.findViewById(R.id.rl_news0);
        newsTV0 = (TextView)view.findViewById(R.id.textview_news_content0);
        dateTV0 = (TextView)view.findViewById(R.id.textview_news_date0);

        rl1 = (RelativeLayout)view.findViewById(R.id.rl_news1);
        newsTV1 = (TextView)view.findViewById(R.id.textview_news_content1);
        dateTV1 = (TextView)view.findViewById(R.id.textview_news_date1);
        seperateLine1 = view.findViewById(R.id.line1);

        rl2 = (RelativeLayout)view.findViewById(R.id.rl_news2);
        newsTV2 = (TextView)view.findViewById(R.id.textview_news_content2);
        dateTV2 = (TextView)view.findViewById(R.id.textview_news_date2);
        seperateLine2 = view.findViewById(R.id.line2);

        rl3 = (RelativeLayout)view.findViewById(R.id.rl_news3);
        newsTV3 = (TextView)view.findViewById(R.id.textview_news_content3);
        dateTV3 = (TextView)view.findViewById(R.id.textview_news_date3);
        seperateLine3 = view.findViewById(R.id.line3);

        rl4 = (RelativeLayout)view.findViewById(R.id.rl_news4);
        newsTV4 = (TextView)view.findViewById(R.id.textview_news_content4);
        dateTV4 = (TextView)view.findViewById(R.id.textview_news_date4);
        seperateLine4 = view.findViewById(R.id.line4);
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
