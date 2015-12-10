package com.tradehero.th.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.tradehero.chinabuild.data.AppInfoDTO;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.chinabuild.fragment.ShareDialogFragment;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.auth.EmailAuthenticationProvider;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.authentication.AuthenticationFragment;
import com.tradehero.th.fragments.authentication.EmailSignInFragment;
import com.tradehero.th.fragments.authentication.EmailSignInOrUpFragment;
import com.tradehero.th.fragments.authentication.EmailSignUpFragment;
import com.tradehero.th.fragments.authentication.PasswordResetFragment;
import com.tradehero.th.fragments.authentication.SignInFragment;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.user.auth.CredentialsDTOFactory;
import com.tradehero.th.models.user.auth.EmailCredentialsDTO;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.QQUtils;
import com.tradehero.th.utils.WeChatUtils;
import com.tradehero.th.utils.WeiboUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.wxapi.WXEntryActivity;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class AuthenticationActivity extends AppCompatActivity
        implements View.OnClickListener, DashboardNavigatorActivity {
    private static final String M_FRAGMENT = "M_CURRENT_FRAGMENT";

    private Map<Integer, Class<?>> mapViewFragment = new HashMap<>();
    private Fragment currentFragment;

    private ProgressDialog progressDialog;
    @Inject Lazy<WeiboUtils> weiboUtils;
    @Inject Lazy<QQUtils> qqUtils;
    @Inject Lazy<WeChatUtils> wechatUtils;
    @Inject Analytics analytics;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject CurrentActivityHolder currentActivityHolder;
    @Inject CredentialsDTOFactory credentialsDTOFactory;

    public boolean isClickedWeChat = false;//fixed bug for wechat 2 times clicked and crash.

    //Application Version Update Dialog
    private Dialog updateAppDialog;
    private TextView dialogOKBtn;
    private TextView dialogCancelBtn;
    private TextView dialogTitleATV;
    private TextView dialogTitleBTV;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    public final static String INTENT_APPLICATION_VERSION_UPDATE = "intent_application_version_update";
    private IntentFilter filter;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(INTENT_APPLICATION_VERSION_UPDATE)) {
                gotoDownloadAppInfo();
                return;
            }
        }
    };

    private Toolbar toolbar;
    private DashboardNavigator navigator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);

        currentActivityHolder.setCurrentActivity(this);

        navigator = new DashboardNavigator(this, getSupportFragmentManager(), R.id.realtabcontent);

        // check if there is a saved fragment, restore it
        if (savedInstanceState != null) {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, M_FRAGMENT);
        }

        if (currentFragment == null) {
            currentFragment = Fragment.instantiate(this, SignInFragment.class.getName(), null);
        }

        setupViewFragmentMapping();

        setContentView(R.layout.authentication_layout);

        toolbar = (Toolbar) findViewById(R.id.th_toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, currentFragment)
                .commit();
        filter = new IntentFilter();
        filter.addAction(INTENT_APPLICATION_VERSION_UPDATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWeChatAccessToken();
        isClickedWeChat = false;
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    /**
     * map view and the next fragment, which is appears when click on that view
     */
    private void setupViewFragmentMapping() {
        //button in SignInFragment
        mapViewFragment.put(R.id.authentication_email_sign_in_link, EmailSignInFragment.class);
        mapViewFragment.put(R.id.tvHeadRight0, EmailSignUpFragment.class);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().putFragment(outState, M_FRAGMENT, currentFragment);
            }
        } catch (Exception ex) {
            Timber.e("Error saving current Authentication Fragment", ex);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        weiboUtils.get().authorizeCallBack(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {

        Class<?> fragmentClass = mapViewFragment.get(view.getId());
        if (fragmentClass != null) {
            setCurrentFragmentByClass(fragmentClass);
            if (currentFragment instanceof AuthenticationFragment) {
                THUser.setAuthenticationMode(((AuthenticationFragment) currentFragment).getAuthenticationMode());
                return;
            }
        }
        THUser.clearCurrentUser();
        switch (view.getId()) {
            case R.id.authentication_sign_up_button:
            case R.id.btn_login:
                authenticateWithEmail();
                break;
            case R.id.btn_weibo_signin:
                THUser.setAuthenticationMode(AuthenticationMode.SignIn);
                authenticateWithWeibo();
                break;
            case R.id.btn_qq_signin:
                THUser.setAuthenticationMode(AuthenticationMode.SignIn);
                authenticateWithQQ();
                break;
            case R.id.btn_wechat_signin:
                THUser.setAuthenticationMode(AuthenticationMode.SignIn);
                if (!isClickedWeChat) {
                    startWeChatSign();
                }
                break;

            case R.id.txt_term_of_service:
                Uri uri = Uri.parse(Constants.PRIVACY_TERMS_OF_SERVICE);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(it);
                } catch (android.content.ActivityNotFoundException anfe) {
                    THToast.show("Unable to open url: " + uri);
                }
                break;
            case R.id.tvHeadLeft:
                onBackPressed();
                break;
        }
    }

    private void setCurrentFragmentByClass(Class<?> fragmentClass) {
        currentFragment = Fragment.instantiate(this, fragmentClass.getName(), null);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_right_in, R.anim.slide_left_out,
                        R.anim.slide_left_in, R.anim.slide_right_out)
                .replace(R.id.fragment_content, currentFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void setCurrentFragmentByPopBack(Class<?> fragmentClass) {
        getSupportFragmentManager().popBackStack();
    }

    private void authenticateWithEmail() {
        if (currentFragment instanceof EmailSignInOrUpFragment) {
            progressDialog = progressDialogUtil.show(this, R.string.alert_dialog_please_wait, R.string.authentication_connecting_tradehero_only);
            EmailSignInOrUpFragment castedFragment = (EmailSignInOrUpFragment) currentFragment;
            JSONCredentials createdJson = castedFragment.getUserFormJSON();
            EmailAuthenticationProvider.setCredentials(createdJson);
            AuthenticationMode authenticationMode = castedFragment.getAuthenticationMode();
            THUser.setAuthenticationMode(authenticationMode);
            THUser.logInWithAsync(EmailCredentialsDTO.EMAIL_AUTH_TYPE, createCallbackForEmailSign(authenticationMode));
        } else {
            throw new IllegalArgumentException("Expected an EmailSignUpFragment or EmailSignInFragment");
        }
    }

    private LogInCallback createCallbackForEmailSign(final AuthenticationMode authenticationMode) {
        final boolean isSigningUp = authenticationMode == AuthenticationMode.SignUp;
        return new SocialAuthenticationCallback(AnalyticsConstants.LOGIN_USER_ACCOUNT) {
            private final boolean signingUp = isSigningUp;

            @Override
            public boolean isSigningUp() {
                return signingUp;
            }

            @Override
            public boolean onSocialAuthDone(JSONCredentials json) {
                return true;
            }
        };
    }

    /**
     * Chinese
     */
    public void authenticateWithWeibo() {
        analytics.addEvent(new MethodEvent(AnalyticsConstants.SIGN_IN, AnalyticsConstants.BUTTON_LOGIN_WEIBO));
        progressDialog = progressDialogUtil.show(this, R.string.alert_dialog_please_wait, R.string.authentication_connecting_to_weibo);
        weiboUtils.get().logIn(this, new SocialAuthenticationCallback(AnalyticsConstants.BUTTON_LOGIN_WEIBO));
    }

    public void authenticateWithQQ() {
        analytics.addEvent(new MethodEvent(AnalyticsConstants.SIGN_IN, AnalyticsConstants.BUTTON_LOGIN_QQ));
        progressDialog = progressDialogUtil.show(this, R.string.alert_dialog_please_wait, R.string.authentication_connecting_to_qq);
        qqUtils.get().logIn(this, new SocialAuthenticationCallback(AnalyticsConstants.BUTTON_LOGIN_QQ));
    }

    public void startWeChatSign() {
        Intent intent = new Intent(this, WXEntryActivity.class);
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.type = WeChatMessageType.Auth;
        WXEntryActivity.putWeChatDTO(intent, weChatDTO);
        startActivity(intent);
    }

    public void authenticateWithWechat(String code) {
        analytics.addEvent(new MethodEvent(AnalyticsConstants.SIGN_IN, AnalyticsConstants.BUTTON_LOGIN_WECHAT));
        progressDialog = progressDialogUtil.show(this, R.string.alert_dialog_please_wait, R.string.authentication_connecting_to_wechat);
        wechatUtils.get().logIn(this, new SocialAuthenticationCallback(AnalyticsConstants.BUTTON_LOGIN_WECHAT), code);
    }

    private void launchDashboard(UserLoginDTO userLoginDTO) {
        THSharePreferenceManager.clearDialogShowedRecord();

        int userId = userLoginDTO.profileDTO.id;
        if (userId <= 0 || THSharePreferenceManager.isRecommendedStock(userId, this)) {
            Intent intent = new Intent(this, TradeHeroMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(UserLoginDTO.SUGGEST_UPGRADE, userLoginDTO.suggestUpgrade);
            intent.putExtra(UserLoginDTO.SUGGEST_LI_REAUTH, userLoginDTO.suggestLiReauth);
            intent.putExtra(UserLoginDTO.SUGGEST_TW_REAUTH, userLoginDTO.suggestTwReauth);
            intent.putExtra(UserLoginDTO.SUGGEST_FB_REAUTH, userLoginDTO.suggestFbReauth);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        } else {
            Intent intent = new Intent(this, RecommendStocksActivity.class);
            intent.putExtra(RecommendStocksActivity.LOGIN_USER_ID, userId);
            startActivity(intent);
        }
        finish();
    }

    private class SocialAuthenticationCallback extends LogInCallback {
        private final String providerName;

        public SocialAuthenticationCallback(String providerName) {
            this.providerName = providerName;
        }

        @Override
        public void done(UserLoginDTO user, THException ex) {
            Throwable cause;
            Response response;
            if (user != null) {
                analytics.addEvent(new MethodEvent(AnalyticsConstants.SIGN_IN_SUCCESSFULLY, providerName));
                launchDashboard(user);
            } else if ((cause = ex.getCause()) != null && cause instanceof RetrofitError &&
                    (response = ((RetrofitError) cause).getResponse()) != null && response.getStatus() == 403) // Forbidden
            {
                THToast.show(R.string.authentication_not_registered);
            } else {
                THToast.show(ex);
            }

            progressDialog.hide();
        }

        @Override
        public boolean onSocialAuthDone(JSONCredentials json) {
            if (!isSigningUp()) {
                // HACK
                if (!AnalyticsConstants.LOGIN_USER_ACCOUNT.equals(providerName)) {
                    if (AuthenticationActivity.this == null) {
                        return false;
                    }
                    String thirdPartyName = providerName;
                    if (providerName.equals(AnalyticsConstants.BUTTON_LOGIN_WECHAT)) {
                        thirdPartyName = getResources().getString(R.string.sign_in_wechat);
                    }
                    if (providerName.equals(AnalyticsConstants.BUTTON_LOGIN_WEIBO)) {
                        thirdPartyName = getResources().getString(R.string.sign_in_weibo);
                    }

                    progressDialog.setMessage(String.format(getString(R.string.authentication_connecting_tradehero), thirdPartyName));
                } else {
                    progressDialog.setMessage(getString(R.string.authentication_connecting_tradehero_only));
                }
                progressDialog.show();
                return true;
            }
            return false;
        }

        @Override
        public void onStart() {
            progressDialog.setMessage(getString(R.string.authentication_connecting_tradehero_only));
        }

        public boolean isSigningUp() {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentDashboardFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_content);
        if (currentDashboardFragment instanceof PasswordResetFragment) {
            super.onBackPressed();
            return;
        }
        if (currentFragment instanceof SignInFragment) {
            ActivityHelper.presentFromActivity(this, GuideActivity.class);
            finish();
            return;
        }
        if (currentFragment instanceof EmailSignInOrUpFragment) {
            getDashboardNavigator().popFragment();
            setCurrentFragmentByPopBack(SignInFragment.class);
            currentFragment = Fragment.instantiate(this, SignInFragment.class.getName(), null);
            return;
        }
        super.onBackPressed();
    }

    public void getWeChatAccessToken() {
        String wechatCode = WXEntryActivity.getWeChatCode();
        if (!TextUtils.isEmpty(wechatCode)) {
            authenticateWithWechat(wechatCode);
            WXEntryActivity.setWeChatCodeNull();
        }
    }


    //Download app info to check whether update TradeHero or not.
    private void gotoDownloadAppInfo() {
        userServiceWrapper.get().downloadAppVersionInfo(new THCallback<AppInfoDTO>() {
            @Override
            protected void success(AppInfoDTO appInfoDTO, THResponse thResponse) {
                {
                    if (appInfoDTO == null || AuthenticationActivity.this == null) {
                        return;
                    }
                    boolean suggestUpdate = appInfoDTO.isSuggestUpgrade();
                    boolean forceUpdate = appInfoDTO.isForceUpgrade();
                    String url = appInfoDTO.getLatestVersionDownloadUrl();
                    THSharePreferenceManager.saveUpdateAppUrlLastestVersionCode(AuthenticationActivity.this, url, suggestUpdate, forceUpdate);
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
        if (!updateAppDialog.isShowing()) {
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

    @Override
    public DashboardNavigator getDashboardNavigator() {
        return navigator;
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }
}
