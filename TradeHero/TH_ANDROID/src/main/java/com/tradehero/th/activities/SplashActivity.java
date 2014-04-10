package com.tradehero.th.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.crashlytics.android.Crashlytics;
import com.facebook.AppEventsLogger;
import com.localytics.android.LocalyticsSession;
import com.tapstream.sdk.*;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tapstream.sdk.*;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.operator.FacebookAppId;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.market.ExchangeListCache;
import com.tradehero.th.persistence.prefs.SessionToken;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.LocalyticsConstants;
import com.tradehero.th.utils.TabStreamEvents;
import com.tradehero.th.utils.VersionUtils;
import dagger.Lazy;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;
import retrofit.RetrofitError;

public class SplashActivity extends SherlockActivity
{
    public static final String TAG = SplashActivity.class.getSimpleName();

    private Timer timerToShiftActivity;
    private AsyncTask<Void, Void, Void> initialAsyncTask;
    @Inject protected UserService userService;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected ExchangeListCache exchangeListCache;
    @Inject @FacebookAppId String facebookAppId;

    @Inject @SessionToken StringPreference currentSessionToken;
    @Inject Lazy<LocalyticsSession> localyticsSession;
    @Inject Lazy<Tapstream> tabStream;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (Constants.RELEASE)
        {
            Crashlytics.start(this);
        }
        setContentView(R.layout.splash_screen);

        DaggerUtils.inject(this);
    }

    @Override protected void onResume()
    {
        super.onResume();
        localyticsSession.get().open();

        AppEventsLogger.activateApp(this, facebookAppId);

        initialAsyncTask = new AsyncTask<Void, Void, Void>()
        {
            @Override protected Void doInBackground(Void... params)
            {
                initialisation();
                return null;
            }
        };
        initialAsyncTask.execute();

        if (!Constants.RELEASE)
        {
            VersionUtils.logScreenMeasurements(this);
        }

        tabStream.get().fireEvent(new Event(TabStreamEvents.APP_OPENED, false));
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

    public boolean canLoadApp()
    {
        boolean canLoad = currentSessionToken.isSet() && currentUserId.toUserBaseKey().key != 0;
        try
        {
            UserProfileDTO profileDTO = userService.getUser(currentUserId.toUserBaseKey().key);
            canLoad &= profileDTO != null && profileDTO.id == currentUserId.toUserBaseKey().key;
            try
            {
                exchangeListCache.getOrFetch(new ExchangeListType());
            }
            catch (Throwable throwable)
            {
                throwable.printStackTrace();
            }
        }
        catch (RetrofitError retrofitError)
        {
            canLoad = false;
        }
        return canLoad;
    }


    @Override protected void onDestroy()
    {
        initialAsyncTask = null;
        super.onDestroy();
    }
}
