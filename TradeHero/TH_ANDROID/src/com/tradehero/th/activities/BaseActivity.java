package com.tradehero.th.activities;

import com.tradehero.th.R;
import com.tradehero.th.base.THUser;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import com.tradehero.th.application.App;
import com.tradehero.th.models.Token;

public class BaseActivity extends Activity
{

    private Timer timerToShiftActivity;
    public static String LOGGEDIN = "_logged";

    @Override
    protected void onCreate(Bundle savedInstanceState)
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
                    startActivity(new Intent(BaseActivity.this, DashboardActivity.class).putExtra(LOGGEDIN, true));
                }
                else
                {
                    startActivity(new Intent(BaseActivity.this, AuthenticationActivity.class));
                }

                finish();
            }
        }, 3000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fullscreen, menu);
        return true;
    }
}
