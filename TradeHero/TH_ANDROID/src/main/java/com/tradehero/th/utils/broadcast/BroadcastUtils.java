package com.tradehero.th.utils.broadcast;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

public class BroadcastUtils
{
    private static final int DELAY_INTERVAL = 5000;
    private static final int MAX_BROADCAST_TRY = 4;
    private final String intentActionName;
    private final String broadcastBundleKey;

    private BroadcastData mUserAchievementId;
    private Handler mHandler;
    private LocalBroadcastManager mLocalBroadcastManager;
    private volatile int mTry;
    public volatile boolean isRunning;

    private Runnable mTask = new Runnable()
    {
        @Override public void run()
        {
            if (mTry >= MAX_BROADCAST_TRY)
            {
                stop();
            }
            else if (!broadcast(mUserAchievementId))
            {
                mHandler.postDelayed(mTask, DELAY_INTERVAL);
                mTry++;
            }
            else
            {
                stop();
            }
        }
    };

    public BroadcastUtils(BroadcastData broadcastData, LocalBroadcastManager mLocalBroadcastManager, String intentActionName, String broadcastBundleKey)
    {
        this.mUserAchievementId = broadcastData;
        this.mLocalBroadcastManager = mLocalBroadcastManager;
        this.intentActionName = intentActionName;
        this.broadcastBundleKey = broadcastBundleKey;
        if (Looper.myLooper() == null)
        {
            Looper.prepare();
        }
        mHandler = new Handler();
    }

    public int getCurrentTry()
    {
        return mTry;
    }

    private boolean broadcast(BroadcastData broadcastData)
    {
        Intent i = new Intent(intentActionName);
        i.putExtra(broadcastBundleKey, broadcastData.getArgs());
        return mLocalBroadcastManager.sendBroadcast(i);
    }

    public void start()
    {
        isRunning = true;
        mTask.run();
    }

    public void stop()
    {
        mHandler.removeCallbacks(mTask);
        isRunning = false;
    }
}
