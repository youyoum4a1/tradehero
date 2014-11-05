package com.tradehero.th.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.crashlytics.android.Crashlytics;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.chinabuild.*;
import com.tradehero.th.fragments.chinabuild.data.LoginContinuallyTimesDTO;
import com.tradehero.th.fragments.chinabuild.data.THSharePreferenceManager;
import com.tradehero.th.models.push.DeviceTokenHelper;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.prefs.BindGuestUser;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.*;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import java.util.Date;

public class MainActivity extends SherlockFragmentActivity implements DashboardNavigatorActivity
{
    //@Inject Lazy<FacebookUtils> facebookUtils;
    @Inject Lazy<WeiboUtils> weiboUtils;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject CurrentActivityHolder currentActivityHolder;
    @Inject Lazy<AlertDialogUtil> alertDialogUtil;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtil;
    @Inject DeviceTokenHelper deviceTokenHelper;
    @Inject SystemStatusCache systemStatusCache;
    private ProgressDialog progressDialog;
    @Inject Analytics analytics;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    @Inject Lazy<PushNotificationManager> pushNotificationManager;
    @Inject @BindGuestUser BooleanPreference mBindGuestUserPreference;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;

    @InjectView(R.id.llMainTab) LinearLayout llMainTab;
    @InjectView(R.id.llTabTrade) LinearLayout llTabTrade;
    @InjectView(R.id.llTabStockGod) LinearLayout llTabStockGod;
    @InjectView(R.id.llTabDiscovery) LinearLayout llTabDiscovery;
    @InjectView(R.id.llTabCompetition) LinearLayout llTabCompetition;
    @InjectView(R.id.llTabMe) LinearLayout llTabMe;
    @InjectView(R.id.linearlayout_guide)LinearLayout guideView;

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

    private FragmentTabHost frg_tabHost;

    private int currentTab = -1;
    private static final int TAB_TRADE = 0;
    private static final int TAB_STOCKGOD = 1;
    private static final int TAB_DISCOVERY = 2;
    private static final int TAB_COMPETITION = 3;
    private static final int TAB_ME = 4;

    public long TIME_PRESSED_BACK = -1;
    /**
     * 定义数组来存放Fragment界面
     */
    private Class fragmentArray[] = {
            MainTabFragmentTrade.class,
            MainTabFragmentStockGod.class,
            MainTabFragmentDiscovery.class,
            MainTabFragmentCompetition.class,
            MainTabFragmentMe.class,
    };

    /**
     * Tab选项卡的文字
     */
    private int strTabArray[] = {
            R.string.tab_main_trade,
            R.string.tab_main_stock_god,
            R.string.tab_main_descovery,
            R.string.tab_main_competition,
            R.string.tab_main_me
    };

    //Guide View
    public final static int GUIDE_TYPE_COMPETITION = 1;
    public final static int GUIDE_TYPE_STOCK_DETAIL = 2;

    public static int guide_current = -1;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        AppTiming.dashboardCreate = System.currentTimeMillis();
        // this need tobe early than super.onCreate or it will crash
        // when device scroll into landscape.
        // request the progress-bar feature for the activity
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
        currentActivityHolder.setCurrentActivity(this);
        tabInit();
        userProfileCacheListener = createUserProfileFetchListener();
        userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
        //enable baidu push
        pushNotificationManager.get().enablePush();
        mBindGuestUserPreference.set(false);

        initGuideView();

        analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MAIN_PAGE_STOCK));

        gotoGetTimesContinuallyLogin();
    }

    @OnClick({R.id.llTabTrade, R.id.llTabStockGod, R.id.llTabDiscovery, R.id.llTabCompetition, R.id.llTabMe})
    public void OnClickTabMenu(View view)
    {
        int id = view.getId();
        switch (id)
        {
            case R.id.llTabTrade:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MAIN_PAGE_TRADE));
                setTabCurrent(TAB_TRADE);
                recordShowedGuideOfMainTab(0);
                break;
            case R.id.llTabStockGod:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MAIN_PAGE_STOCK));
                setTabCurrent(TAB_STOCKGOD);
                break;
            case R.id.llTabDiscovery:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MAIN_PAGE_DISCOVERY));
                setTabCurrent(TAB_DISCOVERY);
                recordShowedGuideOfMainTab(2);
                break;
            case R.id.llTabCompetition:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MAIN_PAGE_COMPETITION));
                setTabCurrent(TAB_COMPETITION);
                recordShowedGuideOfMainTab(3);
                break;
            case R.id.llTabMe:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.MAIN_PAGE_MINE));
                setTabCurrent(TAB_ME);
                break;
        }
    }

    public void setTabCurrent(int index)
    {
        if (index != currentTab)
        {
            currentTab = index;
            frg_tabHost.setCurrentTab(currentTab);
            setTabViewAsChecked();
        }
    }

    public void setTabViewAsChecked()
    {
        imgTabMenu0.setBackgroundResource(
                currentTab == TAB_TRADE ? R.drawable.tab_menu0_active : R.drawable.tab_menu0_normal);
        imgTabMenu1.setBackgroundResource(currentTab == TAB_STOCKGOD ? R.drawable.tab_menu1_active
                : R.drawable.tab_menu1_normal);
        imgTabMenu2.setBackgroundResource(currentTab == TAB_DISCOVERY ? R.drawable.tab_menu2_active : R.drawable.tab_menu2_normal);
        imgTabMenu3.setBackgroundResource(currentTab == TAB_COMPETITION ? R.drawable.tab_menu3_active : R.drawable.tab_menu3_normal);
        imgTabMenu4.setBackgroundResource(
                currentTab == TAB_ME ? R.drawable.tab_menu4_active : R.drawable.tab_menu4_normal);
        tvTabMenu0.setTextColor(currentTab == TAB_TRADE ? getResources().getColor(R.color.main_tab_text_color_active)
                : getResources().getColor(R.color.main_tab_text_color_default));
        tvTabMenu1.setTextColor(currentTab == TAB_STOCKGOD ? getResources().getColor(R.color.main_tab_text_color_active)
                : getResources().getColor(R.color.main_tab_text_color_default));
        tvTabMenu2.setTextColor(currentTab == TAB_DISCOVERY ? getResources().getColor(R.color.main_tab_text_color_active)
                : getResources().getColor(R.color.main_tab_text_color_default));
        tvTabMenu3.setTextColor(currentTab == TAB_COMPETITION ? getResources().getColor(R.color.main_tab_text_color_active)
                : getResources().getColor(R.color.main_tab_text_color_default));
        tvTabMenu4.setTextColor(
                currentTab == TAB_ME ? getResources().getColor(R.color.main_tab_text_color_active)
                        : getResources().getColor(R.color.main_tab_text_color_default));
    }

    private void tabInit()
    {
        setContentView(R.layout.main_activity_layout);
        ButterKnife.inject(this);

        int count = strTabArray.length;
        frg_tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        frg_tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        for (int i = 0; i < count; i++)
        {
            TextView textView = new TextView(this);
            textView.setText(strTabArray[i]);
            //do not see the indicator text，it is a temp solution.
            textView.setTextSize(0);
            textView.setTextColor(0xFF0000FF);
            TabHost.TabSpec tabSpec = frg_tabHost.newTabSpec(getString(strTabArray[i])).setIndicator(textView);
            frg_tabHost.addTab(tabSpec, fragmentArray[i], null);
        }
        //you can set default by this method
        llTabStockGod.performClick();
    }

    private void detachUserProfileCache()
    {
        userProfileCache.get().unregister(userProfileCacheListener);
    }

    @Override public void onBackPressed()
    {
    }

    @Override protected void onStart()
    {
        super.onStart();
    }

    @Override protected void onResume()
    {
        super.onResume();
        analytics.openSession();
        analytics.tagScreen(AnalyticsConstants.MainActivityScreen);
    }

    @Override protected void onPause()
    {
        analytics.closeSession();
        super.onPause();
    }

    @Override protected void onDestroy()
    {
        if (currentActivityHolder != null)
        {
            currentActivityHolder.unsetActivity(this);
        }
        detachUserProfileCache();
        userProfileCacheListener = null;
        super.onDestroy();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //facebookUtils.get().finishAuthentication(requestCode, resultCode, data);
        weiboUtils.get().authorizeCallBack(requestCode, resultCode, data);
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileFetchListener()
    {
        return new UserProfileFetchListener();
    }

    @Override public DashboardNavigator getDashboardNavigator()
    {
        return null;
    }

    @Override public Navigator getNavigator()
    {
        return null;
    }

    protected class UserProfileFetchListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override
        public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            supportInvalidateOptionsMenu();
            if (value.isVisitor)
            {
                showBindGuestUserDialog();
            }
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {

        }
    }

    @Override public void onLowMemory()
    {
        super.onLowMemory();
        String currentFragmentName =
                getSupportFragmentManager().findFragmentById(R.id.realtabcontent)
                        .getClass()
                        .getName();
        Crashlytics.setString("LowMemoryAt", new Date().toString());
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode ==KeyEvent.KEYCODE_BACK)
        {
            if(guideView.getVisibility()==View.VISIBLE){
                dismissGuideView();
            }else{
                exitApp();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exitApp()
    {
        if (TIME_PRESSED_BACK == -1 || (System.currentTimeMillis() - TIME_PRESSED_BACK) > ConstantsChinaBuild.TIME_TO_EXIT_APP)
        {
            THToast.show(R.string.press_back_again_to_exit);
            TIME_PRESSED_BACK = System.currentTimeMillis();
        }
        else
        {
            killApp();
        }
    }

    private void killApp()
    {
        //System.exit(0);
        ////sendAppToBackground();
        //THUser.clearCurrentUser();
        finish();
    }

    private void sendAppToBackground()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    private void showBindGuestUserDialog()
    {
        alertDialogUtil.get().popWithOkCancelButton(this, R.string.app_name,
                R.string.guest_user_dialog_summary,
                R.string.ok, R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                Intent gotoAuthticationIntent = new Intent(MainActivity.this, AuthenticationActivity.class);
                startActivity(gotoAuthticationIntent);
                finish();
            }
        });
    }

    private void gotoGetTimesContinuallyLogin(){
        userServiceWrapper.get().isLoginThreeTimesContinually(currentUserId.toUserBaseKey().key, new ContinuousLoginCallback());
    }

    private class ContinuousLoginCallback implements Callback<LoginContinuallyTimesDTO>{

        @Override
        public void success(LoginContinuallyTimesDTO dto, Response response) {
            if(dto!=null){
                THSharePreferenceManager.Login_Continuous_Time = dto.continuousCount;
            }
        }

        @Override
        public void failure(RetrofitError retrofitError) {
        }
    }

    //Init Guide View
    public void initGuideView(){
        guideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissGuideView();
            }
        });
        displayGuideOfMainTab();
    }

    private void dismissGuideView(){
        guideView.setVisibility(View.GONE);
        if(guide_current == GUIDE_TYPE_COMPETITION){
            THSharePreferenceManager.setGuideShowed(this, THSharePreferenceManager.GUIDE_COMPETITION);
            return;
        }
        if(guide_current == GUIDE_TYPE_STOCK_DETAIL){
            THSharePreferenceManager.setGuideShowed(this, THSharePreferenceManager.GUIDE_STOCK_DETAIL);
            return;
        }
    }

    public void showGuideView(int type){
        if(type == GUIDE_TYPE_COMPETITION){
            if(frg_tabHost.getCurrentTabTag().equals(getString(R.string.tab_main_competition))){
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currFragment = fragmentManager.findFragmentByTag(frg_tabHost.getCurrentTabTag());
                if(currFragment instanceof MainTabFragmentCompetition){
                    int index = ((MainTabFragmentCompetition) currFragment).getCurrentFragmentItem();
                    if(index==0){
                        guide_current  = GUIDE_TYPE_COMPETITION;
                        View view = View.inflate(this, R.layout.guide_layout_competition, null);
                        guideView.removeAllViews();
                        guideView.addView(view);
                        showGuideViewDelayed();
                    }
                }
            }
            return;
        }
        if(type==GUIDE_TYPE_STOCK_DETAIL){
            if(frg_tabHost.getCurrentTabTag().equals(getString(R.string.tab_main_trade))){
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currFragment = fragmentManager.findFragmentByTag(frg_tabHost.getCurrentTabTag());
                if(currFragment instanceof MainTabFragmentTrade){
                    int index = ((MainTabFragmentTrade)currFragment).getCurrentFragmentItem();
                    if(index == 1){
                        guide_current = GUIDE_TYPE_STOCK_DETAIL;
                        View view = View.inflate(this, R.layout.guide_layout_stock, null);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        view.setLayoutParams(params);
                        guideView.removeAllViews();
                        guideView.addView(view);
                        showGuideViewDelayed();
                    }
                }
            }
        }
    }

    private void showGuideViewDelayed(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(guideView.getVisibility() ==View.GONE){
                    guideView.setVisibility(View.VISIBLE);
                }
            }
        }, 100);
    }

    private void displayGuideOfMainTab(){
        if(THSharePreferenceManager.isGuideAvailable(this, THSharePreferenceManager.GUIDE_MAIN_TAB_ZERO)){
            guideTab0IV.setVisibility(View.VISIBLE);
        }else{
            guideTab0IV.setVisibility(View.GONE);
        }

        if(THSharePreferenceManager.isGuideAvailable(this, THSharePreferenceManager.GUIDE_MAIN_TAB_TWO)){
            guideTab2IV.setVisibility(View.VISIBLE);
        }else{
            guideTab2IV.setVisibility(View.GONE);
        }

        if(THSharePreferenceManager.isGuideAvailable(this, THSharePreferenceManager.GUIDE_MAIN_TAB_THREE)){
            guideTab3IV.setVisibility(View.VISIBLE);
        }else{
            guideTab3IV.setVisibility(View.GONE);
        }
    }

    private void recordShowedGuideOfMainTab(int index){
        if(index == 0){
            THSharePreferenceManager.setGuideShowed(this, THSharePreferenceManager.GUIDE_MAIN_TAB_ZERO);
            guideTab0IV.setVisibility(View.GONE);
            return;
        }
        if(index == 2){
            THSharePreferenceManager.setGuideShowed(this, THSharePreferenceManager.GUIDE_MAIN_TAB_TWO);
            guideTab2IV.setVisibility(View.GONE);
            return;
        }
        if(index == 3){
            THSharePreferenceManager.setGuideShowed(this, THSharePreferenceManager.GUIDE_MAIN_TAB_THREE);
            guideTab3IV.setVisibility(View.GONE);
            return;
        }
    }

}
