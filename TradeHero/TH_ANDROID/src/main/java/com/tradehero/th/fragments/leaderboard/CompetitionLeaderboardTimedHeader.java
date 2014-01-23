package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tradehero.common.utils.THLog;
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
    public static final long DEFAULT_UPDATE_MILLISEC_INTERVAL = 200;

    protected TimeDisplayViewHolder timeDisplayViewHolder;
    protected Date futureDateToCountDownTo;
    protected Runnable viewUpdater;

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
        timeDisplayViewHolder = new TimeDisplayViewHolder(getContext());
        futureDateToCountDownTo = new Date();
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        timeDisplayViewHolder.fetchViews(getRootView());
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        viewUpdater = new Runnable()
        {
            @Override public void run()
            {
                timeDisplayViewHolder.showDuration(futureDateToCountDownTo);
                postUpdateDurationIfCan();
            }
        };
        postUpdateDurationIfCan();
    }

    @Override protected void onDetachedFromWindow()
    {
        getHandler().removeCallbacks(viewUpdater);
        viewUpdater = null;
        super.onDetachedFromWindow();
    }

    public void postUpdateDurationIfCan()
    {
        postDelayed(viewUpdater, DEFAULT_UPDATE_MILLISEC_INTERVAL);
    }

    public void setFutureDateToCountDownTo(Date futureDateToCountDownTo)
    {
        this.futureDateToCountDownTo = futureDateToCountDownTo;
    }
}
