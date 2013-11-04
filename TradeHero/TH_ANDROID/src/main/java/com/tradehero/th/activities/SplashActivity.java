package com.tradehero.th.activities;

import com.actionbarsherlock.app.SherlockActivity;
import com.tradehero.th.R;
import com.tradehero.th.base.THUser;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;

public class SplashActivity extends SherlockActivity
{

    public static final String LOGGEDIN = SplashActivity.class.getName();
    private Timer timerToShiftActivity;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);
        timerToShiftActivity = new Timer();
        timerToShiftActivity.schedule(new TimerTask()
        {
            public void run()
            {
                timerToShiftActivity.cancel();
                if (THUser.getSessionToken() != null)
                {
                    ActivityHelper.goRoot(SplashActivity.this);
                }
                else
                {
                    ActivityHelper.doStart(SplashActivity.this);
                }

                finish();
            }
        }, 1000);
    }
}
