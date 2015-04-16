package com.tradehero.th.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.SherlockActivity;
import com.crashlytics.android.Crashlytics;
import com.mobileapptracker.MobileAppTracker;
import com.tapstream.sdk.Api;
import com.tapstream.sdk.Event;
import com.tendcloud.tenddata.TCAgent;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.api.users.LoginFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.models.user.auth.CredentialsDTO;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.network.retrofit.RequestHeaders;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.prefs.FirstLaunch;
import com.tradehero.th.persistence.prefs.ShareDialogKey;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.VersionUtils;
import com.tradehero.th.utils.metrics.MetricsModule;
import dagger.Lazy;
import retrofit.RetrofitError;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends SherlockActivity
{
    private Timer timerToShiftActivity;
    private AsyncTask<Void, Void, Void> initialAsyncTask;
    @Inject SessionServiceWrapper sessionServiceWrapper;
    @Inject RequestHeaders requestHeaders;
    @Inject Provider<LoginFormDTO> loginFormDTOProvider;
    @Inject @FirstLaunch BooleanPreference firstLaunchPreference;

    @Inject MainCredentialsPreference mainCredentialsPreference;
    @Inject Lazy<Api> tapStream;
    @Inject MobileAppTracker mobileAppTracker;
    @Inject CurrentActivityHolder currentActivityHolder;
    @Inject DTOCacheUtil dtoCacheUtil;
    @Inject @ShareDialogKey BooleanPreference mShareDialogKeyPreference;
    @InjectView(R.id.tips) TextView mTipsText;

    private int userId = -1;

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
        ButterKnife.inject(this);
        currentActivityHolder.setCurrentActivity(this);
        mShareDialogKeyPreference.set(true);
        if (mTipsText != null)
        {
            String[] sa = getResources().getStringArray(R.array.loading_page_tips);
            mTipsText.setText(sa[(int)Math.floor(Math.random()*10)]);
        }
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

        tapStream.get().fireEvent(new Event(getString(Constants.TAP_STREAM_TYPE.openResId), false));

        mobileAppTracker.setReferralSources(this);
        mobileAppTracker.measureSession();

        TCAgent.init(getApplicationContext(), MetricsModule.TD_APP_ID_KEY, Constants.TAP_STREAM_TYPE.name());

        if (!Constants.RELEASE)
        {
            VersionUtils.logScreenMeasurements(this);
        }
    }

    protected void initialisation()
    {
        if (firstLaunchPreference.get())
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
                if(userId<=0 || THSharePreferenceManager.isRecommendedStock(userId, this)){
                    ActivityHelper.launchMainActivity(SplashActivity.this);
                }else{
                    Intent intent = new Intent(SplashActivity.this, RecommendStocksActivity.class);
                    intent.putExtra(RecommendStocksActivity.LOGIN_USER_ID, userId);
                    startActivity(intent);
                }
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
                userId = userLoginDTO.profileDTO.id;
            }
            catch (RetrofitError retrofitError)
            {
                canLoad = false;
                if (retrofitError.isNetworkError())
                {
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
