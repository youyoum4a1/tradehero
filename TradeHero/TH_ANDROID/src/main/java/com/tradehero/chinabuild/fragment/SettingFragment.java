package com.tradehero.chinabuild.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.chinabuild.data.AppInfoDTO;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.data.sp.THSharePreferenceManager;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.prefs.ShareDialogAfterScoreKey;
import com.tradehero.th.persistence.prefs.ShareDialogKey;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

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
    @Inject @ShareDialogKey BooleanPreference mShareDialogKeyPreference;
    @Inject @ShareDialogAfterScoreKey BooleanPreference mShareDialogAfterScoreKeyPreference;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;

    @Inject Analytics analytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.settings);
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_score:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.SETTING_SCORE));
                showAppOnMarket();
                //评分后
                //gotoShareScoreDialog()
                break;
            case R.id.settings_faq:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.SETTING_FAQ));
                Uri uri = Uri.parse("http://cn.tradehero.mobi/help/");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(it);
                } catch (android.content.ActivityNotFoundException anfe) {
                    THToast.show("Unable to open url: " + uri);
                }
                break;
            case R.id.settings_logout:
                THUser.clearCurrentUser();
                ActivityHelper.launchAuthentication(getActivity());
                break;
            case R.id.settings_about:
                goToFragment(SettingsAboutUsFragment.class);
                break;
            case R.id.settings_version:
                gotoDownloadAppPage();
                break;
            case R.id.relativelayout_setting_notification:
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.SETTING_NOTIFICAITONS_ON_OFF));
                gotoSetNotifications();
                break;
            case R.id.settings_send_feedback:
                seedFeedback();
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    private void gotoDownloadAppInfo(){
        userServiceWrapper.get().downloadAppVersionInfo(createDownloadAppInfoCallback());
    }

    private THCallback<AppInfoDTO> createDownloadAppInfoCallback(){
        return new THCallback<AppInfoDTO>() {
            @Override
            protected void success(AppInfoDTO appInfoDTO, THResponse thResponse) {
                if(appInfoDTO==null){
                    return;
                }
                Timber.d("------> " + appInfoDTO.toString());
                boolean suggestUpdate = appInfoDTO.isSuggestUpgrade();
                boolean forceUpdate = appInfoDTO.isForceUpgrade();
                String url = appInfoDTO.getLatestVersionDownloadUrl();
                THSharePreferenceManager.saveUpdateAppUrlLastestVersionCode(getActivity(), url, suggestUpdate,forceUpdate);
                if(mVersionLayout==null||mNewVersionImageView==null||mVersionCode==null){
                    return;
                }
                if(suggestUpdate){
                    mVersionLayout.setClickable(true);
                    mNewVersionImageView.setVisibility(View.VISIBLE);
                    mVersionCode.setVisibility(View.GONE);
                }else{
                    mVersionLayout.setClickable(false);
                    mNewVersionImageView.setVisibility(View.GONE);
                    mVersionCode.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void failure(THException ex) {
                THToast.show(ex.getMessage());
            }
        };
    }

    private void gotoDownloadAppPage(){
        AppInfoDTO dto = THSharePreferenceManager.getAppVersionInfo(getActivity());
        if(dto==null){
            return;
        }
        String url = dto.getLatestVersionDownloadUrl();
        if(TextUtils.isEmpty(url)){
            return;
        }
        Uri uri = Uri.parse(url.trim());
        Intent gotoWebIntent = new Intent(Intent.ACTION_VIEW, uri);
        getActivity().startActivity(gotoWebIntent);
    }

    private void gotoSetNotifications(){
        Context context = getActivity();
        if(context==null){
            return;
        }
        if(THSharePreferenceManager.isNotificationsOn(context)){
            THSharePreferenceManager.setNotificaitonsStatus(context, false);
            mNotificationTB.setBackgroundResource(R.drawable.setting_notificaitons_off);
        }else{
            THSharePreferenceManager.setNotificaitonsStatus(context, true);
            mNotificationTB.setBackgroundResource(R.drawable.setting_notifications_on);
        }
    }

    private void seedFeedback(){
        try {
            Intent data = new Intent(Intent.ACTION_SENDTO);
            data.setData(Uri.parse("mailto:" + Constants.EMAIL_FEEDBACK));
            startActivity(data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
