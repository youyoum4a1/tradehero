package com.tradehero.th.utils.broadcast;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.google.common.annotations.VisibleForTesting;
import java.util.Timer;
import java.util.TimerTask;
import org.jetbrains.annotations.NotNull;

public class BroadcastTaskNew
{
    private static final int DELAY_INTERVAL = 5000;
    private static final int MAX_BROADCAST_TRY = 4;
    @NotNull private BroadcastData mData;

    @NotNull private LocalBroadcastManager mLocalBroadcastManager;
    @NotNull private final TaskListener mTaskListener;
    @NotNull private Timer timer;
    private volatile int mTry;
    @VisibleForTesting public volatile boolean isRunning;

    //<editor-fold desc="Constructors">
    public BroadcastTaskNew(
            @NotNull BroadcastData broadcastData,
            @NotNull LocalBroadcastManager mLocalBroadcastManager,
            @NotNull TaskListener taskListener)
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

    private boolean broadcast(@NotNull BroadcastData broadcastData)
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

    public static interface TaskListener
    {
        void onStartBroadcast(@NotNull BroadcastData broadcastData);
        void onFinishBroadcast(@NotNull BroadcastData broadcastData, boolean isSuccessful);
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
