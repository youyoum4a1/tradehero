package com.tradehero.th.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.crashlytics.android.Crashlytics;
import com.facebook.AppEventsLogger;
import com.mobileapptracker.MobileAppTracker;
import com.tapstream.sdk.Api;
import com.tapstream.sdk.Event;
import com.tendcloud.tenddata.TCAgent;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.api.users.LoginFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.auth.operator.FacebookAppId;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.models.user.auth.CredentialsDTO;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.network.retrofit.RequestHeaders;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.prefs.FirstLaunch;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.VersionUtils;
import com.tradehero.th.utils.dagger.UxModule;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import dagger.Lazy;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;
import javax.inject.Provider;
import retrofit.RetrofitError;

public class SplashActivity extends SherlockActivity
{
    private Timer timerToShiftActivity;
    private AsyncTask<Void, Void, Void> initialAsyncTask;
    @Inject SessionServiceWrapper sessionServiceWrapper;
    @Inject RequestHeaders requestHeaders;
    @Inject Provider<LoginFormDTO> loginFormDTOProvider;
    @Inject @FacebookAppId String facebookAppId;
    @Inject @FirstLaunch BooleanPreference firstLaunchPreference;

    @Inject MainCredentialsPreference mainCredentialsPreference;
    @Inject Lazy<THLocalyticsSession> localyticsSession;
    @Inject Lazy<Api> tapStream;
    @Inject MobileAppTracker mobileAppTracker;
    @Inject CurrentActivityHolder currentActivityHolder;
    @Inject DTOCacheUtil dtoCacheUtil;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        AppTiming.splashCreate = System.currentTimeMillis();
        super.onCreate(savedInstanceState);

        if (Constants.RELEASE)
        {
            Crashlytics.start(this);
        }
        setContentView(R.layout.splash_screen);

        TextView appVersion = (TextView) findViewById(R.id.app_version);
        if (appVersion != null)
        {
            appVersion.setText(VersionUtils.getAppVersion(this));
        }

        DaggerUtils.inject(this);
        currentActivityHolder.setCurrentActivity(this);
        dtoCacheUtil.anonymousPrefetches();
    }

    @Override protected void onResume()
    {
        super.onResume();

        initialAsyncTask = new AsyncTask<Void, Void, Void>()
        {
            @Override protected Void doInBackground(Void... params)
            {
                initialisation();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
            }
        };
        initialAsyncTask.execute();

        localyticsSession.get().open(Collections.singletonList(Constants.TAP_STREAM_TYPE.name()));
        localyticsSession.get().tagScreen(LocalyticsConstants.Loading);
        AppEventsLogger.activateApp(this, facebookAppId);
        tapStream.get().fireEvent(new Event(getString(Constants.TAP_STREAM_TYPE.openResId), false));
        mobileAppTracker.setReferralSources(this);
        mobileAppTracker.measureSession();
        TCAgent.init(getApplicationContext(), UxModule.TD_APP_ID_KEY, Constants.TAP_STREAM_TYPE.name());

        if (!Constants.RELEASE)
        {
            VersionUtils.logScreenMeasurements(this);
        }
    }

    @Override protected void onPause()
    {
        localyticsSession.get().close(Collections.singletonList(Constants.TAP_STREAM_TYPE.name()));
        localyticsSession.get().upload();

        super.onPause();
    }

    protected void initialisation()
    {
        localyticsSession.get().tagEvent(LocalyticsConstants.AppLaunch);
        localyticsSession.get().tagEvent(LocalyticsConstants.LoadingScreen);

        if (firstLaunchPreference.get().booleanValue())
        {
            ActivityHelper.launchGuide(SplashActivity.this);
            firstLaunchPreference.set(false);
            finish();
        }
        else
        {
            boolean canLoad = canLoadApp();
            if (canLoad)
            {
                ActivityHelper.launchDashboard(SplashActivity.this);
                finish();
            }
            else
            {
                timerToShiftActivity = new Timer();
                timerToShiftActivity.schedule(new TimerTask()
                {
                    public void run()
                    {
                        timerToShiftActivity.cancel();
                        ActivityHelper.launchAuthentication(SplashActivity.this);
                        finish();
                    }
                }, 1500);
            }
        }
    }

    public boolean canLoadApp()
    {
        CredentialsDTO credentialsDTO = mainCredentialsPreference.getCredentials();
        boolean canLoad = credentialsDTO != null;
        if (canLoad)
        {
            try
            {
                UserLoginDTO userLoginDTO = sessionServiceWrapper.login(
                        requestHeaders.createTypedAuthParameters(credentialsDTO),
                        loginFormDTOProvider.get());
                canLoad = userLoginDTO != null && userLoginDTO.profileDTO != null;
            }
            catch (RetrofitError retrofitError)
            {
                canLoad = false;
                if (retrofitError.isNetworkError())
                {
                    //THToast.show(R.string.network_error);
                }
            }
        }
        return canLoad;
    }

    @Override protected void onDestroy()
    {
        initialAsyncTask = null;
        super.onDestroy();
    }
}
