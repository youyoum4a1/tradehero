package com.tradehero.th.utils.broadcast;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.google.common.annotations.VisibleForTesting;
import java.util.Timer;
import java.util.TimerTask;

public class BroadcastTaskNew
{
    private static final int DELAY_INTERVAL = 5000;
    private static final int MAX_BROADCAST_TRY = 4;
    private BroadcastData mData;

    private LocalBroadcastManager mLocalBroadcastManager;
    private final TaskListener mTaskListener;
    private Timer timer;
    private volatile int mTry;
    @VisibleForTesting public volatile boolean isRunning;

    private TimerTask timerTask = new TimerTask()
    {
        @Override public void run()
        {
            if (mTry >= MAX_BROADCAST_TRY)
            {
                stop(false);
            }
            else if (!broadcast(mData))
            {
                mTry++;
            }
            else
            {
                stop(true);
            }
        }
    };

    public BroadcastTaskNew(BroadcastData broadcastData, LocalBroadcastManager mLocalBroadcastManager, TaskListener taskListener)
    {
        this.mData = broadcastData;
        this.mLocalBroadcastManager = mLocalBroadcastManager;
        this.mTaskListener = taskListener;
        timer = new Timer();
    }

    public int getCurrentTry()
    {
        return mTry;
    }

    private boolean broadcast(BroadcastData broadcastData)
    {
        Intent i = new Intent(broadcastData.getBroadcastIntentActionName());
        i.putExtra(broadcastData.getBroadcastBundleKey(), broadcastData.getArgs());
        return mLocalBroadcastManager.sendBroadcast(i);
    }

    public void start()
    {
        mTaskListener.onStartBroadcast(mData);
        timer.scheduleAtFixedRate(timerTask, 0, DELAY_INTERVAL);
    }

    public void stop(boolean isSuccess)
    {
        mTaskListener.onFinishBroadcast(mData, isSuccess);
        timer.cancel();
    }

    public static interface TaskListener
    {
        void onStartBroadcast(BroadcastData broadcastData);
        void onFinishBroadcast(BroadcastData broadcastData, boolean isSuccessful);
    }
}
