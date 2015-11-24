package com.tradehero.th.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.htsec.TradeModule;
import cn.htsec.data.pkg.trade.TradeManager;
import com.igexin.sdk.PushManager;
import com.tradehero.chinabuild.data.AppInfoDTO;
import com.tradehero.chinabuild.data.LoginContinuallyTimesDTO;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.chinabuild.fragment.ShareDialogFragment;
import com.tradehero.chinabuild.fragment.discovery.DiscoverySquareFragment;
import com.tradehero.chinabuild.mainTab.MainTabFragmentBuyWhat;
import com.tradehero.chinabuild.mainTab.MainTabFragmentCompetition;
import com.tradehero.chinabuild.mainTab.MainTabFragmentDiscovery;
import com.tradehero.chinabuild.mainTab.MainTabFragmentMySetting;
import com.tradehero.chinabuild.mainTab.MainTabFragmentTrade;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOKey;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.PositionServiceWrapper;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.network.service.ShareServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.prefs.BindGuestUser;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TradeHeroMainActivity extends AppCompatActivity {
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject CurrentActivityHolder currentActivityHolder;
    @Inject Lazy<AlertDialogUtil> alertDialogUtil;
    @Inject Analytics analytics;
    @Inject @BindGuestUser BooleanPreference mBindGuestUserPreference;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject Lazy<PositionServiceWrapper> positionServiceWrapper;
    @Inject UserWatchlistPositionCache userWatchlistPositionCache;
    private MiddleCallback<GetPositionsDTO> getPositionDTOCallback;

    @InjectView(R.id.llTabTrade) LinearLayout llTabTrade;
    @InjectView(R.id.llTabStockGod) LinearLayout llTabStockGod;
    @InjectView(R.id.linearlayout_guide) LinearLayout guideView;

    @InjectView(R.id.imgTabMenu0) ImageView imgTabMenu0;
    @InjectView(R.id.imgTabMenu1) ImageView imgTabMenu1;
    @InjectView(R.id.imgTabMenu2) ImageView imgTabMenu2;
    @InjectView(R.id.imgTabMenu3) ImageView imgTabMenu3;
    @InjectView(R.id.imgTabMenu4) ImageView imgTabMenu4;

    @InjectView(R.id.tvTabMenu0) TextView tvTabMenu0;
    @InjectView(R.id.tvTabMenu1) TextView tvTabMenu1;
    @InjectView(R.id.tvTabMenu2) TextView tvTabMenu2;
    @InjectView(R.id.tvTabMenu3) TextView tvTabMenu3;
    @InjectView(R.id.tvTabMenu4) TextView tvTabMenu4;

    @InjectView(R.id.imageview_main_tab0_record) ImageView guideTab0IV;
    @InjectView(R.id.imageview_main_tab2_record) ImageView guideTab2IV;
    @InjectView(R.id.imageview_main_tab3_record) ImageView guideTab3IV;
    @InjectView(R.id.imageview_main_tab4_record) ImageView guideTab4IV;

    @Inject ShareServiceWrapper shareServiceWrapper;

    //GETUI PUSH
    @Inject SessionServiceWrapper sessionServiceWrapper;

    private FragmentTabHost frg_tabHost;
    private static GetPositionsDTO getPositionsDTO;

    private int currentTab = -1;
    private static final int TAB_TRADE = 0;
    private static final int TAB_STOCKGOD = 1;
    private static final int TAB_DISCOVERY = 2;
    private static final int TAB_COMPETITION = 3;
    private static final int TAB_MINE = 4;

    public long TIME_PRESSED_BACK = -1;
    public static final long TIME_TO_EXIT_APP = 1000;

    /**
     * 定义数组来存放Fragment界面
     */
    private Class fragmentArray[] = {
            MainTabFragmentTrade.class,
            MainTabFragmentBuyWhat.class,
            MainTabFragmentDiscovery.class,
            MainTabFragmentCompetition.class,
            MainTabFragmentMySetting.class
    };

    /**
     * Tab选项卡的文字
     */
    private int strTabArray[] = {
            R.string.tab_main_trade,
            R.string.tab_main_buy_what,
            R.string.tab_main_descovery,
            R.string.tab_main_competition,
            R.string.tab_main_me
    };

    //Guide View
    public final static int GUIDE_TYPE_COMPETITION = 1;
    public final static int GUIDE_TYPE_STOCK_DETAIL = 2;
    public static int guide_current = -1;

    //Application Version Update Dialog
    private Dialog updateAppDialog;
    private TextView dialogOKBtn;
    private TextView dialogCancelBtn;
    private TextView dialogTitleATV;
    private TextView dialogTitleBTV;

    public final static int ACTIVITY_RESULT_HAITONG_TRADE = 999;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AppTiming.dashboardCreate = System.currentTimeMillis();
        // this need tobe early than super.onCreate or it will crash
        // when device scroll into landscape.
        // request the progress-bar feature for the activity
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
        currentActivityHolder.setCurrentActivity(this);
        tabInit();

        DiscoverySquareFragment.SHOW_ADVERTISEMENT = true;

        //Download TradeHero Version
        gotoDownloadAppInfo();
        ShareDialogFragment.isDialogShowing = true;

        if(Constants.isInHAITONGTestingEnvironment) {
            TradeModule.debug();
        }

        //show guest user dialog
        UserProfileDTO userProfileDTO = userProfileCache.get().get(currentUserId.toUserBaseKey());
        if ((userProfileDTO != null) && userProfileDTO.isVisitor) {
            alertDialogUtil.get().popWithOkCancelButton(this, R.string.app_name,
                    R.string.guest_user_dialog_summary,
                    R.string.ok, R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent gotoAuthticationIntent = new Intent(TradeHeroMainActivity.this, AuthenticationActivity.class);
                            startActivity(gotoAuthticationIntent);
                            finish();
                        }
                    });
        }

        //enable baidu push
        mBindGuestUserPreference.set(false);

        //Guide View
        guideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissGuideView();
            }
        });
        displayGuideOfMainTab();

        analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MAIN_PAGE_STOCK));

        //Download number of days login continually.
        gotoGetTimesContinuallyLogin();

        getPositionDirectly(currentUserId.toUserBaseKey());
        userWatchlistPositionCache.get(currentUserId.toUserBaseKey());

        //Download Endpoint
        downloadEndPoint();

        // SDK初始化，第三方程序启动时，都要进行SDK初始化工作
        PushManager.getInstance().initialize(this.getApplicationContext());

        //Update GETUI
        updateGETUIID();
    }

    @OnClick({R.id.llTabTrade, R.id.llTabStockGod, R.id.llTabDiscovery, R.id.llTabCompetition, R.id.llTabMine})
    public void OnClickTabMenu(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.llTabTrade:
                setTabCurrent(TAB_TRADE);
                recordShowedGuideOfMainTab(0);
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MAIN_PAGE_TRADE));
                break;
            case R.id.llTabStockGod:
                setTabCurrent(TAB_STOCKGOD);
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MAIN_PAGE_STOCK));
                break;
            case R.id.llTabDiscovery:
                setTabCurrent(TAB_DISCOVERY);
                recordShowedGuideOfMainTab(2);
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MAIN_PAGE_DISCOVERY));
                break;
            case R.id.llTabMine:
                setTabCurrent(TAB_MINE);
                recordShowedGuideOfMainTab(3);
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MAIN_PAGE_MINE));
                break;
            case R.id.llTabCompetition:
                setTabCurrent(TAB_COMPETITION);
                recordShowedGuideOfMainTab(4);
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MAIN_PAGE_COMPETITION));
                break;
        }
    }

    public void setTabCurrent(int index) {
        if (index != currentTab) {
            currentTab = index;
            frg_tabHost.setCurrentTab(currentTab);

            imgTabMenu0.setBackgroundResource(currentTab == TAB_TRADE ? R.drawable.tab_menu0_active : R.drawable.tab_menu0_normal);
            imgTabMenu1.setBackgroundResource(currentTab == TAB_STOCKGOD ? R.drawable.tab_menu1_active : R.drawable.tab_menu1_normal);
            imgTabMenu2.setBackgroundResource(currentTab == TAB_DISCOVERY ? R.drawable.tab_menu2_active : R.drawable.tab_menu2_normal);
            imgTabMenu3.setBackgroundResource(currentTab == TAB_MINE ? R.drawable.tab_menu4_active : R.drawable.tab_menu4_normal);
            imgTabMenu4.setBackgroundResource(currentTab == TAB_COMPETITION ? R.drawable.tab_menu5_active : R.drawable.tab_menu5_normal);

            tvTabMenu0.setTextColor(currentTab == TAB_TRADE ? getResources().getColor(R.color.main_tab_text_color_active)
                    : getResources().getColor(R.color.main_tab_text_color_default));
            tvTabMenu1.setTextColor(currentTab == TAB_STOCKGOD ? getResources().getColor(R.color.main_tab_text_color_active)
                    : getResources().getColor(R.color.main_tab_text_color_default));
            tvTabMenu2.setTextColor(currentTab == TAB_DISCOVERY ? getResources().getColor(R.color.main_tab_text_color_active)
                    : getResources().getColor(R.color.main_tab_text_color_default));
            tvTabMenu3.setTextColor(currentTab == TAB_MINE ? getResources().getColor(R.color.main_tab_text_color_active)
                    : getResources().getColor(R.color.main_tab_text_color_default));
            tvTabMenu4.setTextColor(currentTab == TAB_COMPETITION ? getResources().getColor(R.color.main_tab_text_color_active)
                    : getResources().getColor(R.color.main_tab_text_color_default));
        }
    }

    private void tabInit() {
        setContentView(R.layout.main_activity_layout);
        ButterKnife.inject(this);

        int count = strTabArray.length;
        frg_tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        frg_tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        for (int i = 0; i < count; i++) {
            TextView textView = new TextView(this);
            textView.setText(strTabArray[i]);
            //do not see the indicator text，it is a temp solution.
            textView.setTextSize(0);
            textView.setTextColor(0xFF0000FF);
            TabHost.TabSpec tabSpec = frg_tabHost.newTabSpec(getString(strTabArray[i])).setIndicator(textView);
            frg_tabHost.addTab(tabSpec, fragmentArray[i], null);
        }

        //When a novice first login, jump to leaderboards.
        if (THSharePreferenceManager.isFirstLoginSuccess(this, currentUserId.toUserBaseKey().getUserId())) {
            llTabStockGod.performClick();
        } else {
            llTabTrade.performClick();
        }
    }

    @Override
    protected void onDestroy() {
        if (currentActivityHolder != null) {
            currentActivityHolder.unsetActivity(this);
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TradeHeroMainActivity.ACTIVITY_RESULT_HAITONG_TRADE) {
            if(TradeManager.getInstance(this).isLogined()) {
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (guideView.getVisibility() == View.VISIBLE) {
                dismissGuideView();
            } else {
                exitApp();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exitApp() {
        if (TIME_PRESSED_BACK == -1 || (System.currentTimeMillis() - TIME_PRESSED_BACK) > TIME_TO_EXIT_APP) {
            THToast.show(R.string.press_back_again_to_exit);
            TIME_PRESSED_BACK = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    private void gotoGetTimesContinuallyLogin() {
        userServiceWrapper.get().isLoginThreeTimesContinually(currentUserId.toUserBaseKey().key, new Callback<LoginContinuallyTimesDTO>() {

            @Override
            public void success(LoginContinuallyTimesDTO loginContinuallyTimesDTO, Response response) {
                if (loginContinuallyTimesDTO != null) {
                    THSharePreferenceManager.Login_Continuous_Time = loginContinuallyTimesDTO.continuousCount;
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void dismissGuideView() {
        guideView.setVisibility(View.GONE);
        if (guide_current == GUIDE_TYPE_COMPETITION) {
            THSharePreferenceManager.setGuideShowed(this, THSharePreferenceManager.GUIDE_COMPETITION);
            return;
        }
        if (guide_current == GUIDE_TYPE_STOCK_DETAIL) {
            THSharePreferenceManager.setGuideShowed(this, THSharePreferenceManager.GUIDE_STOCK_DETAIL);
            return;
        }
    }

    public void showGuideView(int type) {
        if (type == GUIDE_TYPE_COMPETITION) {
            if (frg_tabHost.getCurrentTabTag().equals(getString(R.string.tab_main_competition))) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currFragment = fragmentManager.findFragmentByTag(frg_tabHost.getCurrentTabTag());
                if (currFragment instanceof MainTabFragmentCompetition) {
                    int index = ((MainTabFragmentCompetition) currFragment).getCurrentFragmentItem();
                    if (index == 0) {
                        guide_current = GUIDE_TYPE_COMPETITION;
                        View view = View.inflate(this, R.layout.guide_layout_competition, null);
                        guideView.removeAllViews();
                        guideView.addView(view);
                        showGuideViewDelayed();
                    }
                }
            }
            return;
        }
        if (type == GUIDE_TYPE_STOCK_DETAIL) {
            if (frg_tabHost.getCurrentTabTag().equals(getString(R.string.tab_main_trade))) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currFragment = fragmentManager.findFragmentByTag(frg_tabHost.getCurrentTabTag());
                if (currFragment instanceof MainTabFragmentTrade) {
                    int index = ((MainTabFragmentTrade) currFragment).getCurrentFragmentItem();
                    if (index == 1) {
                        guide_current = GUIDE_TYPE_STOCK_DETAIL;
                        View view = View.inflate(this, R.layout.guide_layout_stock, null);
                        RelativeLayout.LayoutParams params =
                                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        view.setLayoutParams(params);
                        guideView.removeAllViews();
                        guideView.addView(view);
                        showGuideViewDelayed();
                    }
                }
            }
        }
    }

    private void showGuideViewDelayed() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (guideView.getVisibility() == View.GONE) {
                    guideView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void displayGuideOfMainTab() {
        if (THSharePreferenceManager.isGuideAvailable(this, THSharePreferenceManager.GUIDE_MAIN_TAB_ZERO)) {
            guideTab0IV.setVisibility(View.VISIBLE);
        } else {
            guideTab0IV.setVisibility(View.GONE);
        }

        if (THSharePreferenceManager.isGuideAvailable(this, THSharePreferenceManager.GUIDE_MAIN_TAB_TWO)) {
            guideTab2IV.setVisibility(View.VISIBLE);
        } else {
            guideTab2IV.setVisibility(View.GONE);
        }

        if (THSharePreferenceManager.isGuideAvailable(this, THSharePreferenceManager.GUIDE_MAIN_TAB_THREE)) {
            guideTab3IV.setVisibility(View.VISIBLE);
        } else {
            guideTab3IV.setVisibility(View.GONE);
        }
        if (THSharePreferenceManager.isGuideAvailable(this, THSharePreferenceManager.GUIDE_MAIN_TAB_FOUR)) {
            guideTab4IV.setVisibility(View.VISIBLE);
        } else {
            guideTab4IV.setVisibility(View.GONE);
        }
    }

    private void recordShowedGuideOfMainTab(int index) {
        if (index == 0) {
            THSharePreferenceManager.setGuideShowed(this, THSharePreferenceManager.GUIDE_MAIN_TAB_ZERO);
            guideTab0IV.setVisibility(View.GONE);
            return;
        }
        if (index == 2) {
            THSharePreferenceManager.setGuideShowed(this, THSharePreferenceManager.GUIDE_MAIN_TAB_TWO);
            guideTab2IV.setVisibility(View.GONE);
            return;
        }
        if (index == 3) {
            THSharePreferenceManager.setGuideShowed(this, THSharePreferenceManager.GUIDE_MAIN_TAB_THREE);
            guideTab3IV.setVisibility(View.GONE);
            return;
        }
        if (index == 4) {
            THSharePreferenceManager.setGuideShowed(this, THSharePreferenceManager.GUIDE_MAIN_TAB_FOUR);
            guideTab4IV.setVisibility(View.GONE);
            return;
        }
    }

    protected void getPositionDirectly(@NotNull UserBaseKey heroId) {
        if (getPositionDTOCallback != null) {
            getPositionDTOCallback.setPrimaryCallback(null);
        }
        getPositionDTOCallback = null;
        getPositionDTOCallback = positionServiceWrapper.get()
                .getPositionsDirect(heroId.key, 1, 20, new Callback<GetPositionsDTO>() {

                    @Override
                    public void success(GetPositionsDTO getPositionsDTO, Response response) {
                        setGetPositionDTO(getPositionsDTO);
                        TradeHeroMainActivity.getPositionsDTO = getPositionsDTO;
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

    public static PositionDTOKey getSecurityPositionDTOKey(SecurityId securityId) {
        GetPositionsDTO getPositionsDTO = TradeHeroMainActivity.getPositionsDTO;
        if (getPositionsDTO == null)
            return null;
        PositionDTO positionDTO = getPositionsDTO.getSecurityPositionDTO(securityId);
        if (positionDTO != null)
            return positionDTO.getPositionDTOKey();
        return null;
    }

    public static PositionDTOKey getSecurityPositionDTOKey(int securityId) {
        GetPositionsDTO getPositionsDTO = TradeHeroMainActivity.getPositionsDTO;
        if (getPositionsDTO == null)
            return null;
        PositionDTO positionDTO = getPositionsDTO.getSecurityPositionDTO(securityId);
        if (positionDTO != null)
            return positionDTO.getPositionDTOKey();
        return null;
    }

    public static void setGetPositionDTO(GetPositionsDTO getPositionsDTO) {
        TradeHeroMainActivity.getPositionsDTO = getPositionsDTO;
    }

    private void downloadEndPoint() {
        shareServiceWrapper.getShareEndPoint(new Callback<String>() {
            @Override
            public void success(String endpoint, Response response) {
                if (TextUtils.isEmpty(endpoint)) {
                    return;
                }
                if (TradeHeroMainActivity.this != null) {
                    THSharePreferenceManager.setShareEndpoint(TradeHeroMainActivity.this, endpoint);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    //Download app info to check whether update TradeHero or not.
    private void gotoDownloadAppInfo() {
        userServiceWrapper.get().downloadAppVersionInfo(new THCallback<AppInfoDTO>() {
            @Override
            protected void success(AppInfoDTO appInfoDTO, THResponse thResponse) {
                {
                    if (appInfoDTO == null || TradeHeroMainActivity.this == null) {
                        return;
                    }
                    boolean suggestUpdate = appInfoDTO.isSuggestUpgrade();
                    boolean forceUpdate = appInfoDTO.isForceUpgrade();
                    String url = appInfoDTO.getLatestVersionDownloadUrl();
                    THSharePreferenceManager.saveUpdateAppUrlLastestVersionCode(TradeHeroMainActivity.this, url, suggestUpdate, forceUpdate);
                    if (suggestUpdate || forceUpdate) {
                        showUpdateDialog();
                    } else {
                        ShareDialogFragment.isDialogShowing = false;
                    }
                }
            }

            @Override
            protected void failure(THException ex) {
                ShareDialogFragment.isDialogShowing = false;
            }

        });
    }

    private void showUpdateDialog() {
        if (updateAppDialog == null) {
            updateAppDialog = new Dialog(this);
            updateAppDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            updateAppDialog.setCanceledOnTouchOutside(false);
            updateAppDialog.setCancelable(false);
            updateAppDialog.setContentView(R.layout.share_dialog_layout);
            dialogOKBtn = (TextView) updateAppDialog.findViewById(R.id.btn_ok);
            dialogCancelBtn = (TextView) updateAppDialog.findViewById(R.id.btn_cancel);
            dialogTitleATV = (TextView) updateAppDialog.findViewById(R.id.title);
            dialogTitleATV.setText(getResources().getString(R.string.app_update_hint));
            dialogTitleBTV = (TextView) updateAppDialog.findViewById(R.id.title2);
            final AppInfoDTO dto = THSharePreferenceManager.getAppVersionInfo(this);
            if (dto.isForceUpgrade()) {
                dialogTitleBTV.setText(getResources().getString(R.string.app_update_force_update));
            } else {
                dialogTitleBTV.setVisibility(View.GONE);
            }
            dialogOKBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShareDialogFragment.isDialogShowing = false;
                    String url = dto.getLatestVersionDownloadUrl();
                    if (dto.isForceUpgrade() || dto.isSuggestUpgrade()) {
                        downloadApp(url);
                    } else {
                        updateAppDialog.dismiss();
                    }
                }
            });

            dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateAppDialog.dismiss();
                    ShareDialogFragment.isDialogShowing = false;
                    if (dto.isForceUpgrade()) {
                        finish();
                    }
                }
            });
        }
        if (!updateAppDialog.isShowing() && TradeHeroMainActivity.this != null) {
            updateAppDialog.show();
        }
    }

    private void downloadApp(String url) {
        ActivityHelper.launchBrowserDownloadApp(this, url);
        if (updateAppDialog != null) {
            updateAppDialog.dismiss();
        }
        finish();
    }

    private void updateGETUIID() {
        final String getuiid = THSharePreferenceManager.getGETUIID(this);
        if (getuiid.equals("")) {
            return;
        }
        sessionServiceWrapper.updateDevice(getuiid, new Callback() {
            @Override
            public void success(Object o, Response response) {
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }
}
