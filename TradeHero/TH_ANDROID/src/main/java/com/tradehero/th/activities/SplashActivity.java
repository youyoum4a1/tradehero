package com.tradehero.th.activities;

import android.content.Intent;
import com.actionbarsherlock.app.SherlockActivity;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.base.THUser;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;

public class SplashActivity extends SherlockActivity
{
    public static final String TAG = SplashActivity.class.getSimpleName();

    private Timer timerToShiftActivity;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (THUser.getSessionToken() != null)
        {
            ActivityHelper.goRoot(SplashActivity.this);
            finish();
        }
        else
        {
            setContentView(R.layout.splash_screen);

            timerToShiftActivity = new Timer();
            timerToShiftActivity.schedule(new TimerTask()
            {
                public void run()
                {
                    timerToShiftActivity.cancel();
                    ActivityHelper.doStart(SplashActivity.this);
                    finish();
                }
            }, 1500);
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        THLog.d(TAG, "onActivityResult " + requestCode + ", " + resultCode + ", " + data);
    }
}
