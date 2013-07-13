package android.tradehero.Activities;

import java.util.Timer;
import java.util.TimerTask;


import android.tradehero.Activities.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class BaseActivity extends Activity {
	
	Timer timerToShiftActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    		
        
        /*GridView gridView = (GridView)findViewById(R.id.gridview);
        gridView.setAdapter(new TrendingAdapter(this));*/
        
        
        setContentView(R.layout.splash_screen);
		timerToShiftActivity = new Timer();

		// schedule the timer task to be done after specified time period
		timerToShiftActivity.schedule(new TimerTask() {

			
			public void run() {
				
				
							

				timerToShiftActivity.cancel();
				startActivity(new Intent(BaseActivity.this,WelcomeActivity.class));
				finish();

			}
		}, 1000);
        
        
        //,,,,,,,,,,,,,,,,
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fullscreen, menu);
        return true;
    }
    
}
