package com.tradehero.th.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.auth.FacebookAuthenticationProvider;
import com.tradehero.th.auth.TwitterAuthenticationProvider;
import com.tradehero.th.auth.linkedin.LinkedInAuthenticationProvider;
import com.tradehero.th.auth.tencent_qq.QQAuthenticationProvider;
import com.tradehero.th.auth.weibo.WeiboAuthenticationProvider;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.authentication.SignInOrUpFragment;
import com.tradehero.th.inject.Injector;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.dagger.AppModule;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class AuthenticationActivity extends BaseActivity
        implements Injector
{
    private ProgressDialog progressDialog;

    @Inject Lazy<WeiboAuthenticationProvider> weiboAuthenticationProviderLazy;
    @Inject Lazy<QQAuthenticationProvider> qqUtils;
    @Inject Analytics analytics;
    @Inject ProgressDialogUtil progressDialogUtil;
    private DashboardNavigator navigator;
    @Inject FacebookAuthenticationProvider facebookAuthenticationProvider;
    @Inject LinkedInAuthenticationProvider linkedInAuthenticationProvider;
    @Inject TwitterAuthenticationProvider twitterAuthenticationProvider;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_layout);

        setupNavigator();

        navigator.pushFragment(SignInOrUpFragment.class, new Bundle());
    }

    private void setupNavigator()
    {
        navigator = new DashboardNavigator(this, R.id.fragment_content);
    }

    @Override protected void onResume()
    {
        super.onResume();
        analytics.openSession();
        analytics.tagScreen(AnalyticsConstants.Login_Register);
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.LoginRegisterScreen));
    }

    @Override protected List<Object> getModules()
    {
        List<Object> superModules = new ArrayList<>(super.getModules());
        superModules.add(new AuthenticationActivityModule());
        return superModules;
    }

    @Override protected void onPause()
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
        }
        analytics.closeSession();
        super.onPause();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d("onActivityResult %d, %d, %s", requestCode, resultCode, data);
        facebookAuthenticationProvider.onActivityResult(requestCode, resultCode, data);
        weiboAuthenticationProviderLazy.get().authorizeCallBack(requestCode, resultCode, data);
    }

    //<editor-fold desc="Authenticate with Facebook/Twitter/LinkedIn">

    public void authenticateWithLinkedIn()
    {
        analytics.addEvent(new MethodEvent(AnalyticsConstants.SignUp_Tap, AnalyticsConstants.Linkedin));
        progressDialog = progressDialogUtil.show(this, R.string.alert_dialog_please_wait, R.string.authentication_connecting_to_linkedin);

        // FIXME/refactor
        linkedInAuthenticationProvider.logIn(this);
    }

    public void authenticateWithFacebook()
    {
        analytics.addEvent(new MethodEvent(AnalyticsConstants.SignUp_Tap, AnalyticsConstants.Facebook));
        progressDialog = progressDialogUtil.show(this, R.string.alert_dialog_please_wait, R.string.authentication_connecting_to_facebook);

        // FIXME/refactor
        facebookAuthenticationProvider.logIn(this);

        throw new RuntimeException("FIXME/refactor");
    }

    public void authenticateWithTwitter()
    {
        analytics.addEvent(new MethodEvent(AnalyticsConstants.SignUp_Tap, AnalyticsConstants.Twitter));
        progressDialog = progressDialogUtil.show(this, R.string.alert_dialog_please_wait, R.string.authentication_twitter_connecting);
        // FIXME/refactor
        twitterAuthenticationProvider.logIn(this);

        throw new RuntimeException("FIXME/refactor");
    }

    private void launchDashboard(UserLoginDTO userLoginDTO)
    {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra(UserLoginDTO.SUGGEST_UPGRADE, userLoginDTO.suggestUpgrade);
        intent.putExtra(UserLoginDTO.SUGGEST_LI_REAUTH, userLoginDTO.suggestLiReauth);
        intent.putExtra(UserLoginDTO.SUGGEST_TW_REAUTH, userLoginDTO.suggestTwReauth);
        intent.putExtra(UserLoginDTO.SUGGEST_FB_REAUTH, userLoginDTO.suggestFbReauth);

        startActivity(intent);
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        finish();
    }
    //</editor-fold>

    @Override protected boolean requireLogin()
    {
        return false;
    }

    @Module(
            addsTo = AppModule.class,
            library = true,
            complete = false,
            overrides = true
    )
    public class AuthenticationActivityModule
    {
        @Provides DashboardNavigator provideDashboardNavigator()
        {
            return navigator;
        }
    }
}
