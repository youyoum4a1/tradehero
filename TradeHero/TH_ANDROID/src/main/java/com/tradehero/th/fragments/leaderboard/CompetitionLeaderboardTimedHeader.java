package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tradehero.th.widget.time.TimeDisplayViewHolder;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xavier on 1/23/14.
 */
public class CompetitionLeaderboardTimedHeader extends LinearLayout
{
    public static final String TAG = CompetitionLeaderboardTimedHeader.class.getSimpleName();
    public static final long DEFAULT_UPDATE_MILLISEC_INTERVAL = 500;

    protected TimeDisplayViewHolder timeDisplayViewHolder;
    protected TimerTask updateViewTimerTask;
    protected Timer taskTimer;
    protected Date futureDateToCountDownTo;

    //<editor-fold desc="Constructors">
    public CompetitionLeaderboardTimedHeader(Context context)
    {
        super(context);
        init();
    }

    public CompetitionLeaderboardTimedHeader(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CompetitionLeaderboardTimedHeader(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    //</editor-fold>

    protected void init()
    {
        timeDisplayViewHolder = new TimeDisplayViewHolder();
        futureDateToCountDownTo = new Date();
        updateViewTimerTask = new TimerTask()
        {
            @Override public void run()
            {
                timeDisplayViewHolder.showDuration(futureDateToCountDownTo);
            }
        };
        taskTimer = new Timer();
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        timeDisplayViewHolder.fetchViews(getRootView());
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        taskTimer.schedule(updateViewTimerTask, DEFAULT_UPDATE_MILLISEC_INTERVAL);
    }

    @Override protected void onDetachedFromWindow()
    {
        taskTimer.cancel();
        super.onDetachedFromWindow();
    }

    public void setFutureDateToCountDownTo(Date futureDateToCountDownTo)
    {
        this.futureDateToCountDownTo = futureDateToCountDownTo;
    }
}
