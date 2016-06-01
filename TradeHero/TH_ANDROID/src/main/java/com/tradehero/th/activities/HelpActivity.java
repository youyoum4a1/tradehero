package com.ayondo.academy.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import butterknife.OnClick;
import com.ayondo.academy.R;
import timber.log.Timber;

public class HelpActivity extends AppCompatActivity
{
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    public static void slideInFromRight(@NonNull Activity fromActivity)
    {
        Intent intent = new Intent(fromActivity, HelpActivity.class);
        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeCustomAnimation(fromActivity, R.anim.slide_right_in, R.anim.slide_left_out);
        ActivityCompat.startActivity(fromActivity, intent, optionsCompat.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

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

    @OnClick(R.id.exit_help)
    @Override public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }
}
