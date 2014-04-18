package com.tradehero.th.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.crashlytics.android.Crashlytics;
import com.facebook.AppEventsLogger;
import com.localytics.android.LocalyticsSession;
import com.tapstream.sdk.Event;
import com.tapstream.sdk.Tapstream;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.operator.FacebookAppId;
import com.tradehero.th.base.Application;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.market.ExchangeListCache;
import com.tradehero.th.persistence.prefs.SessionToken;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.tapstream.TapStreamEvents;
import com.tradehero.th.utils.VersionUtils;
import dagger.Lazy;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;
import retrofit.RetrofitError;

public class SplashActivity extends SherlockActivity
{
    public static final String TAG = SplashActivity.class.getSimpleName();
    public static final String KEY_PREFS = SplashActivity.class.getName();
    private static final String KEY_FIRST_BOOT = "key_first_boot";

    private Timer timerToShiftActivity;
    private AsyncTask<Void, Void, Void> initialAsyncTask;
    @Inject protected UserService userService;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected ExchangeListCache exchangeListCache;
    @Inject protected ProviderListCache providerListCache;
    @Inject @FacebookAppId String facebookAppId;

    @Inject @SessionToken StringPreference currentSessionToken;
    @Inject Lazy<LocalyticsSession> localyticsSession;
    @Inject Lazy<Tapstream> tapStream;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (Constants.RELEASE)
        {
            //TODO can't run in alipay branch
            //Crashlytics.start(this);
        }
        setContentView(R.layout.splash_screen);

        TextView appVersion = (TextView) findViewById(R.id.app_version);
        if (appVersion != null)
        {
            appVersion.setText(VersionUtils.getAppVersion(this));
        }

        DaggerUtils.inject(this);
    }

    @Override protected void onResume()
    {
        super.onResume();

        // TODO HAcK to load provider early
        providerListCache.autoFetch(new ProviderListKey());
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

        localyticsSession.get().open();
        AppEventsLogger.activateApp(this, facebookAppId);
        tapStream.get().fireEvent(new Event(TapStreamEvents.APP_OPENED, false));

        if (!Constants.RELEASE)
        {
            VersionUtils.logScreenMeasurements(this);
        }

        tapStream.get().fireEvent(new Event(TapStreamEvents.APP_OPENED, false));
    }

    @Override protected void onPause()
    {
        localyticsSession.get().close();
        localyticsSession.get().upload();

        super.onPause();
    }

    protected void initialisation()
    {
        localyticsSession.get().tagEvent(LocalyticsConstants.AppLaunch);
        SharedPreferences preferences = Application.context().getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);

        if (preferences.getBoolean(KEY_FIRST_BOOT, true))
        {
            ActivityHelper.launchGuide(SplashActivity.this);
            preferences.edit().putBoolean(KEY_FIRST_BOOT, false).apply();
        }
        else
        {
            if (canLoadApp())
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
        // TODO HAcK to ensure DashboardActivity has exchange list
        boolean canLoad = currentSessionToken.isSet() && currentUserId.toUserBaseKey().key != 0;
        try
        {
            UserProfileDTO profileDTO = userService.getUser(currentUserId.get());
            canLoad &= profileDTO != null && profileDTO.id == currentUserId.get();
            try
            {
                exchangeListCache.getOrFetch(new ExchangeListType());
            } catch (Throwable throwable)
            {
                throwable.printStackTrace();
            }
        } catch (RetrofitError retrofitError)
        {
            canLoad = false;
            if (retrofitError.isNetworkError())
            {
                //THToast.show(R.string.network_error);
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
