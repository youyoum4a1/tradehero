package com.tradehero.th.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;
import com.crashlytics.android.Crashlytics;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.persistence.prefs.SessionToken;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.TestFlightUtils;
import com.tradehero.th.utils.VersionUtils;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;

public class SplashActivity extends SherlockActivity
{
    public static final String TAG = SplashActivity.class.getSimpleName();

    private Timer timerToShiftActivity;
    private AsyncTask<Void, Void, Void> initialAsyncTask;

    @Inject @SessionToken StringPreference currentSessionToken;

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
    }

    protected void initialisation()
    {
        TestFlightUtils.initialize();

        if (currentSessionToken.isSet())
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

    @Override protected void onDestroy()
    {
        initialAsyncTask = null;
        super.onDestroy();
    }
}
