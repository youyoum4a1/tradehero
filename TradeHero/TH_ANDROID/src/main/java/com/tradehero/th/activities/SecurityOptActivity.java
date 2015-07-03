package com.tradehero.th.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;

import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created by palmer on 15/7/1.
 */
public class SecurityOptActivity extends FragmentActivity implements View.OnClickListener {

    public final static String BUNDLE_FROM_TYPE = "BUNDLE_FROM_TYPE";
    public final static String TYPE_BUY = "TYPE_BUY";
    public final static String TYPE_SELL = "TYPE_SELL";
    public final static String TYPE_SEARCH = "TYPE_SEARCH";
    public final static String TYPE_RECALL = "TYPE_RECALL";

    private ImageButton searchBtn;
    private ImageButton backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_opt);
        backButton = (ImageButton) findViewById(R.id.button_security_opt_back);
        searchBtn = (ImageButton) findViewById(R.id.button_security_opt_search);
        backButton.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.button_security_opt_back:
                finish();
                overridePendingTransition(R.anim.slide_left_in,R.anim.slide_right_out);
                break;
            case R.id.button_security_opt_search:
                finish();
                gotoDashboard(SearchUnitFragment.class.getName(), new Bundle());
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
                break;


        }
    }

    private void gotoDashboard(String strFragment,Bundle bundle) {
        bundle.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME,strFragment);
        ActivityHelper.launchDashboard(this, bundle);
    }
}
