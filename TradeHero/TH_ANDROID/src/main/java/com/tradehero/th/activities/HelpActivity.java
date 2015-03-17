package com.tradehero.th.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.tradehero.th.R;
import timber.log.Timber;

public class HelpActivity extends ActionBarActivity
{

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        final View exit = findViewById(R.id.exit_help);
        final View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                finish();
            }
        };
        exit.setOnClickListener(onClickListener);

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener()
        {
            @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
            {
                try
                {
                    if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
                    {
                        finish();
                    }
                }
                catch (Exception e)
                {
                    Timber.e("Error", e);
                }
                return false;
            }

            @Override public boolean onDown(MotionEvent e)
            {
                return true;
            }
        });

        findViewById(R.id.view_container).setOnTouchListener(new View.OnTouchListener()
        {
            @Override public boolean onTouch(View v, MotionEvent event)
            {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }
}
