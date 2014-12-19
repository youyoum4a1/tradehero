package com.tradehero.th.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.crashlytics.android.Crashlytics;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.data.sp.THSharePreferenceManager;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.authentication.SignInFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.models.push.DeviceTokenHelper;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.persistence.prefs.FirstShowReferralCodeDialog;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.ui.AppContainer;
import com.tradehero.th.utils.*;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.widget.GuideView;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

import javax.inject.Inject;

public class DashboardActivity extends SherlockFragmentActivity
        implements DashboardNavigatorActivity
{
    @Inject AppContainer appContainer;

    private final DashboardTabType INITIAL_TAB = DashboardTabType.HOME;
    private DashboardNavigator navigator;
    @Inject Lazy<WeiboUtils> weiboUtils;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject CurrentActivityHolder currentActivityHolder;
    @Inject Lazy<AlertDialogUtil> alertDialogUtil;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtil;
    @Inject Lazy<NotificationCache> notificationCache;
    @Inject DeviceTokenHelper deviceTokenHelper;
    @Inject @FirstShowReferralCodeDialog BooleanPreference firstShowReferralCodeDialogPreference;
    @Inject SystemStatusCache systemStatusCache;
    private ProgressDialog progressDialog;
    @Inject Analytics analytics;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;

    //Guide View
    private GuideView guideRL;

    public int SCREEN_W;
    public int SCREEN_H;

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

        if (Constants.RELEASE)
        {
            Crashlytics.setString(Constants.TH_CLIENT_TYPE,
                    String.format("%s:%d", deviceTokenHelper.getDeviceType(), Constants.TAP_STREAM_TYPE.type));
            Crashlytics.setUserIdentifier("" + currentUserId.get());
        }

        //setContentView ...
        ViewGroup dashboardWrapper = appContainer.get(this);
        initViews();
        setScreenWH();
        detachUserProfileCache();
        userProfileCacheListener = createUserProfileFetchListener();

        if (currentUserId.get().toString().length() > 1)
        {
            userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileCacheListener);
            userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
        }

        navigator = new DashboardNavigator(this, getSupportFragmentManager(), R.id.realtabcontent);

        if (savedInstanceState == null && navigator.getCurrentFragment() == null)
        {
            Bundle args = getIntent().getExtras();
            if (args != null)
            {
                String CLASS_NAME = args.getString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME);
                Timber.d("CLASS_NAME = " + CLASS_NAME);
                try
                {
                    Class onwClass = Class.forName(CLASS_NAME);
                    if(onwClass!=null)
                    {
                        navigator.goToFragment(onwClass, args);
                    }
                } catch (Exception e)
                {
                    finish();
                }
            }
        }
    }

    private void detachUserProfileCache()
    {
        userProfileCache.get().unregister(userProfileCacheListener);
    }

    @Override public void onBackPressed()
    {
        if(guideRL.getVisibility()==View.VISIBLE){
            dismissGuideView();
            return;
        }
        if(getNavigator().getCurrentFragment() instanceof SignInFragment){
            ActivityHelper.launchGuide(this);
            return;
        }
        Fragment fragment = getNavigator().getCurrentFragment();
        if(fragment instanceof DashboardFragment){
            DashboardFragment fragmentDF = (DashboardFragment)fragment;
            if(fragmentDF.isNeedBackPressed()){
                fragmentDF.onBackPressed();
                return;
            }
        }
        getNavigator().popFragment();
    }

    private void pushFragmentIfNecessary(Class<? extends Fragment> fragmentClass)
    {
        Fragment currentDashboardFragment = navigator.getCurrentFragment();
        if (!(fragmentClass.isInstance(currentDashboardFragment)))
        {
            getNavigator().pushFragment(fragmentClass);
        }
    }

    @Override protected void onStart()
    {
        super.onStart();
    }

    @Override protected void onResume()
    {
        super.onResume();
        analytics.openSession();
        guideRL.setVisibility(View.GONE);
    }

    @Override protected void onPause()
    {
        analytics.closeSession();
        super.onPause();
    }

    @Override protected void onDestroy()
    {

        if (navigator != null)
        {
            navigator.onDestroy();
        }
        navigator = null;

        if (currentActivityHolder != null)
        {
            currentActivityHolder.unsetActivity(this);
        }

        detachUserProfileCache();
        userProfileCacheListener = null;

        super.onDestroy();
    }

    //Guide Views
    private void initViews(){
        guideRL = (GuideView)findViewById(R.id.guideview_guide_view);
        guideRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissGuideView();
            }
        });
    }

    public void showGuideView(int position_x, int position_y, int radius, int guide_type){
        if(guideRL.getVisibility()==View.GONE){
            guideRL.draw(position_x,position_y, radius, SCREEN_W, SCREEN_H, guide_type);
            guideRL.setVisibility(View.VISIBLE);
        }
    }

    public void dismissGuideView(){
        if(guideRL.getVisibility()==View.VISIBLE){
            guideRL.setVisibility(View.GONE);
            int guideType = guideRL.getType();
            if(guideType == GuideView.TYPE_GUIDE_COMPETITION_JOIN){
                THSharePreferenceManager.setGuideShowed(this, THSharePreferenceManager.GUIDE_COMPETITION_JOIN);
                return;
            }
            if(guideType == GuideView.TYPE_GUIDE_STOCK_BUG){
                THSharePreferenceManager.setGuideShowed(this, THSharePreferenceManager.GUIDE_STOCK_BUY);
                return;
            }
        }
    }

    public boolean isGuideViewShow(){
        return guideRL.getVisibility()==View.VISIBLE;
    }


    //<editor-fold desc="DashboardNavigatorActivity">
    @Override public Navigator getNavigator()
    {
        return navigator;
    }

    @Override public DashboardNavigator getDashboardNavigator()
    {
        return navigator;
    }
    //</editor-fold>

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        weiboUtils.get().authorizeCallBack(requestCode, resultCode, data);
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileFetchListener()
    {
        return new UserProfileFetchListener();
    }

    protected class UserProfileFetchListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override
        public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            supportInvalidateOptionsMenu();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {

        }
    }

    private void setScreenWH() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = this.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        SCREEN_W = screenWidth;
        SCREEN_H = screenHeight;
    }

}
