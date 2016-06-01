package com.ayondo.academy.utils.broadcast;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import com.google.common.annotations.VisibleForTesting;
import java.util.Timer;
import java.util.TimerTask;

public class BroadcastTaskNew
{
    private static final int DELAY_INTERVAL = 5000;
    private static final int MAX_BROADCAST_TRY = 4;
    @NonNull private BroadcastData mData;

    @NonNull private LocalBroadcastManager mLocalBroadcastManager;
    @NonNull private final TaskListener mTaskListener;
    @NonNull private Timer timer;
    private volatile int mTry;
    @VisibleForTesting public volatile boolean isRunning;

    //<editor-fold desc="Constructors">
    public BroadcastTaskNew(
            @NonNull BroadcastData broadcastData,
            @NonNull LocalBroadcastManager mLocalBroadcastManager,
            @NonNull TaskListener taskListener)
    {
        this.mData = broadcastData;
        this.mLocalBroadcastManager = mLocalBroadcastManager;
        this.mTaskListener = taskListener;
        timer = new Timer();
    }
    //</editor-fold>

    public int getCurrentTry()
    {
        return mTry;
    }

    private boolean broadcast(@NonNull BroadcastData broadcastData)
    {
        Intent i = new Intent(broadcastData.getBroadcastIntentActionName());
        i.putExtra(broadcastData.getBroadcastBundleKey(), broadcastData.getArgs());
        return mLocalBroadcastManager.sendBroadcast(i);
    }

    public void start()
    {
        mTaskListener.onStartBroadcast(mData);
        timer.scheduleAtFixedRate(new BroadcastTaskNewTimerTask(), 0, DELAY_INTERVAL);
    }

    public void stop(boolean isSuccess)
    {
        mTaskListener.onFinishBroadcast(mData, isSuccess);
        timer.cancel();
    }

    public interface TaskListener
    {
        void onStartBroadcast(@NonNull BroadcastData broadcastData);

        void onFinishBroadcast(@NonNull BroadcastData broadcastData, boolean isSuccessful);
    }

    protected class BroadcastTaskNewTimerTask extends TimerTask
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
    }
}
