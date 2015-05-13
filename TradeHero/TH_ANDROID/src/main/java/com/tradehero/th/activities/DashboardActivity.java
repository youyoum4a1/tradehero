package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.crashlytics.android.Crashlytics;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.authentication.SignInFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.push.DeviceTokenHelper;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.ui.AppContainer;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.WeiboUtils;
import com.tradehero.th.widget.GuideView;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.Lazy;

public class DashboardActivity extends AppCompatActivity
        implements DashboardNavigatorActivity {
    @Inject AppContainer appContainer;

    private DashboardNavigator navigator;
    @Inject Lazy<WeiboUtils> weiboUtils;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject CurrentActivityHolder currentActivityHolder;
    @Inject DeviceTokenHelper deviceTokenHelper;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;

    //Guide View
    private GuideView guideRL;

    public int SCREEN_W;
    public int SCREEN_H;

    private Toolbar toolbar;

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

        if (Constants.RELEASE) {
            Crashlytics.setString(Constants.TH_CLIENT_TYPE,
                    String.format("%s:%d", deviceTokenHelper.getDeviceType(), Constants.TAP_STREAM_TYPE.type));
            Crashlytics.setUserIdentifier("" + currentUserId.get());
        }

        //setContentView ...
        ViewGroup dashboardWrapper = appContainer.get(this);

        toolbar = (Toolbar) findViewById(R.id.th_toolbar);
        setSupportActionBar(toolbar);

        initViews();
        setScreenWH();
        detachUserProfileCache();
        userProfileCacheListener = createUserProfileFetchListener();

        if (currentUserId.get().toString().length() > 1) {
            userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileCacheListener);
            userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
        }

        navigator = new DashboardNavigator(this, getSupportFragmentManager(), R.id.realtabcontent);

        if (savedInstanceState == null && navigator.getCurrentFragment() == null) {
            Bundle args = getIntent().getExtras();
            if (args != null) {
                String CLASS_NAME = args.getString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME);
                try {
                    Class onwClass = Class.forName(CLASS_NAME);
                    if (onwClass != null) {
                        navigator.goToFragment(onwClass, args);
                    }
                } catch (Exception e) {
                    finish();
                }
            }
        }
    }

    private void detachUserProfileCache() {
        userProfileCache.get().unregister(userProfileCacheListener);
    }

    @Override
    public void onBackPressed() {
        if (guideRL.getVisibility() == View.VISIBLE) {
            dismissGuideView();
            return;
        }
        if (getDashboardNavigator().getCurrentFragment() instanceof SignInFragment) {
            ActivityHelper.presentFromActivity(this, GuideActivity.class);
            finish();
            return;
        }
        Fragment fragment = getDashboardNavigator().getCurrentFragment();
        if (fragment instanceof DashboardFragment) {
            DashboardFragment fragmentDF = (DashboardFragment) fragment;
            if (fragmentDF.isNeedBackPressed()) {
                fragmentDF.onBackPressed();
                return;
            }
        }
        getDashboardNavigator().popFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        guideRL.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if (navigator != null) {
            navigator.onDestroy();
        }
        navigator = null;

        if (currentActivityHolder != null) {
            currentActivityHolder.unsetActivity(this);
        }

        detachUserProfileCache();
        userProfileCacheListener = null;

        super.onDestroy();
    }

    //Guide Views
    private void initViews() {
        guideRL = (GuideView) findViewById(R.id.guideview_guide_view);
        guideRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissGuideView();
            }
        });
    }

    public void showGuideView(int position_x, int position_y, int radius, int guide_type) {
        if (guideRL.getVisibility() == View.GONE) {
            guideRL.draw(position_x, position_y, radius, SCREEN_W, SCREEN_H, guide_type);
            guideRL.setVisibility(View.VISIBLE);
        }
    }

    public void showGuideView(int position_y, int guide_type) {
        if (guideRL.getVisibility() == View.GONE) {
            guideRL.draw(position_y, SCREEN_W, SCREEN_H, guide_type);
            guideRL.setVisibility(View.VISIBLE);
        }
    }

    public void dismissGuideView() {
        if (guideRL.getVisibility() == View.VISIBLE) {
            guideRL.setVisibility(View.GONE);
            int guideType = guideRL.getType();
            if (guideType == GuideView.TYPE_GUIDE_STOCK_BUY) {
                THSharePreferenceManager.setGuideShowed(this, THSharePreferenceManager.GUIDE_STOCK_BUY);
                return;
            }
            if (guideType == GuideView.TYPE_GUIDE_COMPETITION_EDIT) {
                THSharePreferenceManager.setGuideShowed(this, THSharePreferenceManager.GUIDE_COMPETITION_INTRO_EDIT);
            }
        }
    }

    public boolean isGuideViewShow() {
        return guideRL.getVisibility() == View.VISIBLE;
    }


    //<editor-fold desc="DashboardNavigatorActivity">
    @Override
    public DashboardNavigator getDashboardNavigator() {
        return navigator;
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }
    //</editor-fold>

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        weiboUtils.get().authorizeCallBack(requestCode, resultCode, data);
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileFetchListener() {
        return new UserProfileFetchListener();
    }

    protected class UserProfileFetchListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> {
        @Override
        public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value) {
            supportInvalidateOptionsMenu();
        }

        @Override
        public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error) {

        }
    }

    private void setScreenWH() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        SCREEN_W = screenWidth;
        SCREEN_H = screenHeight;
    }

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int height = getResources().getDimensionPixelSize(resourceId);
        return height;
    }

}
