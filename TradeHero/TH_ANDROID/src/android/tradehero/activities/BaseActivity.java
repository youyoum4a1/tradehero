package android.tradehero.activities;

import java.util.Timer;
import java.util.TimerTask;


import android.tradehero.activities.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.tradehero.activities.WelcomeActivity;;
public class BaseActivity extends Activity {
	
	private Timer timerToShiftActivity;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	
        setContentView(R.layout.splash_screen);
		timerToShiftActivity = new Timer();
		timerToShiftActivity.schedule(new TimerTask() 
		{	
			public void run()
			{
				timerToShiftActivity.cancel();
				startActivity(new Intent(BaseActivity.this,WelcomeActivity.class));
				finish();
			}
		}, 3000);
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fullscreen, menu);
        return true;
    }
    
}
