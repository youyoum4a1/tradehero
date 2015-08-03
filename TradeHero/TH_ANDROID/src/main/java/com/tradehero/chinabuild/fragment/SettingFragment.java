package com.tradehero.chinabuild.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.igexin.sdk.PushManager;
import com.tradehero.chinabuild.data.AppInfoDTO;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.activities.AuthenticationActivity;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;

import cn.htsec.data.pkg.trade.TradeManager;
import dagger.Lazy;
import javax.inject.Inject;

public class SettingFragment extends DashboardFragment implements View.OnClickListener {
    @InjectView(R.id.settings_score) RelativeLayout mScoreLayout;
    @InjectView(R.id.settings_faq) RelativeLayout mFaqLayout;
    @InjectView(R.id.imageview_new_version) ImageView mNewVersionImageView;
    @InjectView(R.id.settings_version_code) TextView mVersionCode;
    @InjectView(R.id.settings_version) RelativeLayout mVersionLayout;
    @InjectView(R.id.settings_about) RelativeLayout mAboutLayout;
    @InjectView(R.id.settings_logout) LinearLayout mLogoutLayout;
    @InjectView(R.id.togglebutton_setting_notifications) ToggleButton mNotificationTB;
    @InjectView(R.id.relativelayout_setting_notification) RelativeLayout mNotificationsLayout;
    @InjectView(R.id.settings_send_feedback)RelativeLayout mSeedFeedbackLayout;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;

    @Inject Analytics analytics;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(getString(R.string.settings));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment_layout, container, false);
        ButterKnife.inject(this, view);
        mScoreLayout.setOnClickListener(this);
        mFaqLayout.setOnClickListener(this);
        PackageInfo packageInfo = null;
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            mVersionCode.setText("V" + packageInfo.versionName + "." + packageInfo.versionCode);
        }
        mAboutLayout.setOnClickListener(this);
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if(userProfileDTO!=null){
            if (userProfileDTO.isVisitor) {
                mLogoutLayout.setVisibility(View.GONE);
            }
        }
        mLogoutLayout.setOnClickListener(this);
        mVersionLayout.setOnClickListener(this);
        mSeedFeedbackLayout.setOnClickListener(this);
        mVersionLayout.setClickable(false);
        mNewVersionImageView.setVisibility(View.GONE);
        mVersionCode.setVisibility(View.VISIBLE);
        mNotificationsLayout.setOnClickListener(this);
        if(THSharePreferenceManager.isNotificationsOn(getActivity())){
            mNotificationTB.setBackgroundResource(R.drawable.setting_notifications_on);
        }else{
            mNotificationTB.setBackgroundResource(R.drawable.setting_notificaitons_off);
        }
        gotoDownloadAppInfo();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_score:
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.SETTING_SCORE));
                showAppOnMarket();
                break;
            case R.id.settings_faq:
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.SETTING_FAQ));
                Uri uri = Uri.parse("http://cn.tradehero.mobi/help/");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(it);
                } catch (android.content.ActivityNotFoundException anfe) {
                    THToast.show("Unable to open url: " + uri);
                }
                break;
            case R.id.settings_logout:
                logout();
                break;
            case R.id.settings_about:
                pushFragment(SettingsAboutUsFragment.class, new Bundle());
                break;
            case R.id.settings_version:
                gotoDownloadAppPage();
                break;
            case R.id.relativelayout_setting_notification:
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.SETTING_NOTIFICAITONS_ON_OFF));
                gotoSetNotifications();
                break;
            case R.id.settings_send_feedback:
                ActivityHelper.sendFeedback(getActivity());
                break;
        }
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }


    public void showAppOnMarket() {
        try {
            getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "market://details?id=" + getActivity().getPackageName())));
        } catch (ActivityNotFoundException ex) {
            try {
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id="
                                + getActivity().getPackageName())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Download app info to check whether update TradeHero or not.
    private void gotoDownloadAppInfo(){
        userServiceWrapper.get().downloadAppVersionInfo(new THCallback<AppInfoDTO>() {
            @Override
            protected void success(AppInfoDTO appInfoDTO, THResponse thResponse) {
                if (appInfoDTO == null)  {
                    return;
                }
                boolean suggestUpdate = appInfoDTO.isSuggestUpgrade();
                THSharePreferenceManager.saveUpdateAppUrlLastestVersionCode(getActivity(), appInfoDTO.getLatestVersionDownloadUrl(), suggestUpdate, appInfoDTO.isForceUpgrade());
                if (mVersionLayout == null || mNewVersionImageView == null || mVersionCode == null) {
                    return;
                }
                if (suggestUpdate) {
                    mVersionLayout.setClickable(true);
                    mNewVersionImageView.setVisibility(View.VISIBLE);
                    mVersionCode.setVisibility(View.GONE);
                } else {
                    mVersionLayout.setClickable(false);
                    mNewVersionImageView.setVisibility(View.GONE);
                    mVersionCode.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void failure(THException ex) {

            }
        });
    }

    private void gotoDownloadAppPage(){
        AppInfoDTO dto = THSharePreferenceManager.getAppVersionInfo(getActivity());
        if(dto==null){
            return;
        }
        ActivityHelper.launchBrowserDownloadApp(getActivity(), dto.getLatestVersionDownloadUrl());
    }

    private void gotoSetNotifications(){
        Context context = getActivity();
        if(context==null){
            return;
        }
        if(THSharePreferenceManager.isNotificationsOn(context)){
            THSharePreferenceManager.setNotificationStatus(context, false);
            mNotificationTB.setBackgroundResource(R.drawable.setting_notificaitons_off);
            PushManager.getInstance().turnOffPush(context);
        }else{
            THSharePreferenceManager.setNotificationStatus(context, true);
            mNotificationTB.setBackgroundResource(R.drawable.setting_notifications_on);
            PushManager.getInstance().turnOnPush(context);
        }
    }

    private void logout(){
        THUser.clearCurrentUser();
        ActivityHelper.presentFromActivity(getActivity(), AuthenticationActivity.class);
        getActivity().finish();
        if(TradeManager.getInstance(getActivity()).isLogined()){
            TradeManager.getInstance(getActivity()).logout();
        }
    }

}
