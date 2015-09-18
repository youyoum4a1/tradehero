package com.tradehero.th.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tradehero.chinabuild.fragment.competition.CompetitionSecuritySearchFragment;
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.common.utils.THToast;
import com.tradehero.livetrade.SecurityOptPhoneNumBindFragment;
import com.tradehero.livetrade.data.LiveTradeSessionDTO;
import com.tradehero.livetrade.services.LiveTradeCallback;
import com.tradehero.livetrade.services.LiveTradeManager;
import com.tradehero.livetrade.thirdPartyServices.haitong.HaitongUtils;
import com.tradehero.livetrade.SecurityOptActualFragment;
import com.tradehero.chinabuild.fragment.security.SecurityOptMockFragment;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;

import javax.inject.Inject;

import cn.htsec.data.pkg.trade.TradeManager;

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

    @Inject LiveTradeManager tradeManager;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;

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

    private String securityExchange = "";

    private String hintA = "";
    private String hintB = "";
    private String hintC = "";

    //Trading View
    private RelativeLayout tradingRL;
    private TextView tradingTV;
    public final static String INTENT_START_TRADING = "INTENT_START_TRADING";
    public final static String INTENT_END_TRADING = "INTENT_END_TRADING";
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(INTENT_END_TRADING)){
                stopTradingHint();
            }
            if(action.equals(INTENT_START_TRADING)){
                startTradingHit();
            }
        }
    };
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_opt);

        DaggerUtils.inject(this);

        initArguments();
        initResources();
        initViews();

        FragmentManager fragmentManager = getSupportFragmentManager();
        SecurityOptMockFragment securityOptMockFragment = new SecurityOptMockFragment();
        securityOptMockFragment.setArguments(getIntent().getExtras());
        fragmentManager.beginTransaction().replace(R.id.framelayout_mock_actual, securityOptMockFragment).commit();

        if(isForActual){
            enterActual();
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_END_TRADING);
        intentFilter.addAction(INTENT_START_TRADING);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TradeHeroMainActivity.ACTIVITY_RESULT_HAITONG_TRADE) {
            if(TradeManager.getInstance(this).isLogined()) {
                finish();
                Bundle bundle = new Bundle();
                bundle.putBoolean(SecurityOptActivity.KEY_IS_FOR_ACTUAL, true);
                bundle.putString(SecurityOptActivity.BUNDLE_FROM_TYPE, SecurityOptActivity.TYPE_BUY);
                Intent intent = new Intent(this, SecurityOptActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        }
    }

    @Override
    public void onDestroy(){
        stopTradingHint();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
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
                enterSearchPage();
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

        tradingTV = (TextView)findViewById(R.id.textview_trading);
        tradingRL = (RelativeLayout)findViewById(R.id.relativelayout_trading);
        tradingRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });
    }

    private void initResources(){
        color_actual = getResources().getColor(R.color.number_up);
        color_mock = getResources().getColor(R.color.color_blue);
        color_white = getResources().getColor(R.color.white);
        hintA = getResources().getString(R.string.trading_hint_a);
        hintB = getResources().getString(R.string.trading_hint_b);
        hintC = getResources().getString(R.string.trading_hint_c);
    }

    private void initArguments(){
        competitionId = getIntent().getIntExtra(CompetitionSecuritySearchFragment.BUNDLE_COMPETITION_ID, 0);
        isForActual = getIntent().getBooleanExtra(KEY_IS_FOR_ACTUAL, false);
        if(getIntent().hasExtra(KEY_SECURITY_EXCHANGE)) {
            securityExchange = getIntent().getStringExtra(KEY_SECURITY_EXCHANGE);
        }
    }

    private void gotoDashboard(String strFragment,Bundle bundle) {
        bundle.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME, strFragment);
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
        if(!isMock) {
            return;
        }

        if(!tradeManager.getLiveTradeServices().isSessionValid()) {
            UserProfileDTO profileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
            if (tradeManager.getLiveTradeServices().needCheckPhoneNumber() && profileDTO.phoneNumber == null) {
                registerReceiver(new PhoneBindBroadcastReceiver(),
                        new IntentFilter(SecurityOptPhoneNumBindFragment.INTENT_REFRESH_COMPETITION_DISCUSSIONS));

                gotoDashboard(SecurityOptPhoneNumBindFragment.class.getName(), new Bundle());
            }
            else {
                tradeManager.getLiveTradeServices().login(this, "70000399", "111111", new LiveTradeCallback<LiveTradeSessionDTO>() {
                    @Override
                    public void onSuccess(LiveTradeSessionDTO liveTradeSessionDTO) {
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

                    @Override
                    public void onError(String errorCode, String errorContent) {
                        THToast.show(errorContent);
                    }
                });
            }
        } else {
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

    private void enterSearchPage(){
        finish();
        Bundle bundle = new Bundle();
        if(isMock) {
            if (competitionId != 0) {
                if(getIntent().getExtras().containsKey(SecurityOptActivity.KEY_PORTFOLIO_ID)) {
                    bundle.putBundle(SecurityOptActivity.KEY_PORTFOLIO_ID, getIntent().getExtras().getBundle(SecurityOptActivity.KEY_PORTFOLIO_ID));
                }
                bundle.putBoolean(CompetitionSecuritySearchFragment.BUNDLE_GO_TO_BUY_SELL_DIRECTLY, true);
                bundle.putString(SecurityOptActivity.BUNDLE_FROM_TYPE, SecurityOptActivity.TYPE_BUY);
                bundle.putInt(CompetitionSecuritySearchFragment.BUNDLE_COMPETITION_ID, competitionId);
                gotoDashboard(CompetitionSecuritySearchFragment.class.getName(), bundle);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            } else {
                bundle.putBoolean(CompetitionSecuritySearchFragment.BUNDLE_GO_TO_BUY_SELL_DIRECTLY, true);
                bundle.putString(SecurityOptActivity.BUNDLE_FROM_TYPE, SecurityOptActivity.TYPE_BUY);
                gotoDashboard(SearchUnitFragment.class.getName(), bundle);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        } else {
            Intent intent = new Intent(this, SearchSecurityActualActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        }
    }

    private void startTradingHit(){
        if (tradingTV == null || tradingRL == null) {
            return;
        }
        tradingRL.setVisibility(View.VISIBLE);
        tradingTV.setText(hintA);
        RefreshLoadingHandler handler = new RefreshLoadingHandler();
        handler.sendEmptyMessageDelayed(-1, 1000);
    }

    private void stopTradingHint(){
        if (tradingTV == null || tradingRL == null) {
            return;
        }
        tradingRL.setVisibility(View.GONE);
        tradingTV.setText(hintA);
        RefreshLoadingHandler handler = new RefreshLoadingHandler();
        handler.sendEmptyMessageDelayed(-1, 1000);
    }

    class RefreshLoadingHandler extends Handler {
        public void handleMessage(Message msg) {
            if (tradingTV == null || tradingRL == null) {
                return;
            }
            if (tradingRL.getVisibility() == View.VISIBLE) {
                String tradingStr = tradingTV.getText().toString();
                if (tradingStr.equals(hintA)) {
                    tradingTV.setText(hintB);
                } else if (tradingStr.equals(hintB)) {
                    tradingTV.setText(hintC);
                } else {
                    tradingTV.setText(hintA);
                }
                RefreshLoadingHandler handler = new RefreshLoadingHandler();
                handler.sendEmptyMessageDelayed(-1, 1000);
            }
        }
    }

    class PhoneBindBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            enterActual();
        }
    }
}
