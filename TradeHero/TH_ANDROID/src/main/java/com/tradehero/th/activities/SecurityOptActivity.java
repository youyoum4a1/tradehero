package com.tradehero.th.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tradehero.chinabuild.fragment.competition.CompetitionSecuritySearchFragment;
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.firmbargain.SecurityOptActualFragment;
import com.tradehero.chinabuild.fragment.security.SecurityOptMockFragment;
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

    public final static String KEY_SECURITY_SYMBOL = "KEY_SECURITY_SYMBOL";
    public final static String KEY_SECURITY_EXCHANGE = "KEY_SECURITY_EXCHANGE";
    public final static String KEY_PORTFOLIO_ID = "KEY_PORTFOLIO_ID";
    public final static String KEY_IS_FOR_ACTUAL = "KEY_IS_FOR_ACTUAL";

    private ImageButton searchBtn;
    private ImageButton backButton;
    private TextView mockTV;
    private TextView actualTV;
    private RelativeLayout toolbarRL;

    private int color_actual;
    private int color_mock;
    private int color_white;

    private boolean isMock = true;

    private int competitionId = 0;

    private boolean isForActual = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_opt);
        initArguments();
        initResources();
        initViews();


        FragmentManager fragmentManager = getSupportFragmentManager();
        SecurityOptMockFragment securityOptMockFragment = new SecurityOptMockFragment();
        securityOptMockFragment.setArguments(getIntent().getExtras());
        fragmentManager.beginTransaction().replace(R.id.framelayout_mock_actual, securityOptMockFragment).commit();

        if(isForActual){
            enterActual();
        } else {
            enterMock();
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.button_security_opt_back:
                finish();
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                break;
            case R.id.button_security_opt_search:
                finish();
                Bundle bundle = new Bundle();
                if(competitionId!=0){
                    bundle.putInt(CompetitionSecuritySearchFragment.BUNLDE_COMPETITION_ID, competitionId);
                    gotoDashboard(CompetitionSecuritySearchFragment.class.getName(), bundle);
                    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                } else {
                    gotoDashboard(SearchUnitFragment.class.getName(), bundle);
                    overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                }
                break;
            case R.id.textview_actual:
                enterActual();
                break;
            case R.id.textview_mock:
                enterMock();
                break;

        }
    }

    private void initViews(){
        backButton = (ImageButton) findViewById(R.id.button_security_opt_back);
        searchBtn = (ImageButton) findViewById(R.id.button_security_opt_search);
        backButton.setOnClickListener(this);
        searchBtn.setOnClickListener(this);

        mockTV = (TextView)findViewById(R.id.textview_mock);
        actualTV = (TextView)findViewById(R.id.textview_actual);
        mockTV.setOnClickListener(this);
        actualTV.setOnClickListener(this);
        toolbarRL = (RelativeLayout)findViewById(R.id.relativelayout_security_opt_toolbar);
    }

    private void initResources(){
        color_actual = getResources().getColor(R.color.number_up);
        color_mock = getResources().getColor(R.color.color_blue);
        color_white = getResources().getColor(R.color.white);

    }

    private void initArguments(){
        competitionId = getIntent().getIntExtra(CompetitionSecuritySearchFragment.BUNLDE_COMPETITION_ID , 0);
        isForActual = getIntent().getBooleanExtra(KEY_IS_FOR_ACTUAL, false);
    }

    private void gotoDashboard(String strFragment,Bundle bundle) {
        bundle.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME,strFragment);
        ActivityHelper.launchDashboard(this, bundle);
    }

    private void enterMock(){
        if(isMock){
            return;
        }
        isMock = true;
        mockTV.setTextColor(color_mock);
        mockTV.setBackgroundResource(R.drawable.security_opt_c);
        actualTV.setBackgroundResource(R.drawable.security_opt_d);
        actualTV.setTextColor(color_white);
        toolbarRL.setBackgroundColor(color_mock);

        FragmentManager fragmentManager = getSupportFragmentManager();
        SecurityOptMockFragment securityOptMockFragment = new SecurityOptMockFragment();
        securityOptMockFragment.setArguments(getIntent().getExtras());
        fragmentManager.beginTransaction().replace(R.id.framelayout_mock_actual, securityOptMockFragment).commit();
    }

    private void enterActual(){
        if(!isMock){
            return;
        }
        isMock = false;
        mockTV.setTextColor(color_white);
        mockTV.setBackgroundResource(R.drawable.security_opt_a);
        actualTV.setBackgroundResource(R.drawable.security_opt_b);
        actualTV.setTextColor(color_actual);
        toolbarRL.setBackgroundColor(color_actual);

        FragmentManager fragmentManager = getSupportFragmentManager();
        SecurityOptActualFragment securityOptActualFragment = new SecurityOptActualFragment();
        securityOptActualFragment.setArguments(getIntent().getExtras());
        fragmentManager.beginTransaction().replace(R.id.framelayout_mock_actual, securityOptActualFragment).commit();
    }
}
