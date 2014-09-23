package com.tradehero.th.utils.broadcast;

import android.support.v4.content.LocalBroadcastManager;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class BroadcastUtils implements BroadcastTaskNew.TaskListener
{
    @NotNull private final LocalBroadcastManager localBroadcastManager;
    @NotNull private ArrayDeque<BroadcastData> broadcastQueue = new ArrayDeque<>();
    @NotNull private final AtomicBoolean isProcessing = new AtomicBoolean(false);

    //<editor-fold desc="Constructors">
    @Inject public BroadcastUtils(@NotNull LocalBroadcastManager localBroadcastManager)
    {
        this.localBroadcastManager = localBroadcastManager;
    }
    //</editor-fold>

    public BroadcastTaskNew enqueue(@NotNull BroadcastData broadcastData)
    {
        broadcastQueue.add(broadcastData);
        if (!isProcessing.get())
        {
            return broadcast(broadcastQueue.pop());
        }
        else
        {
            return null;
        }
    }

    public void nextPlease()
    {
        if (isProcessing.get())
        {
            isProcessing.set(false);
            if (!broadcastQueue.isEmpty())
            {
                broadcast(broadcastQueue.pop());
            }
        }
    }

    private BroadcastTaskNew broadcast(@NotNull BroadcastData broadcastData)
    {
        isProcessing.set(true);
        BroadcastTaskNew task = new BroadcastTaskNew(broadcastData, localBroadcastManager, this);
        task.start();
        return task;
    }

    @Override public void onStartBroadcast(@NotNull BroadcastData broadcastData)
    {
        isProcessing.compareAndSet(false, true);
    }

    @Override public void onFinishBroadcast(@NotNull BroadcastData broadcastData, boolean isSuccessful)
    {
        if (!isSuccessful)
        {
            broadcastQueue.addLast(broadcastData);
            isProcessing.set(false);
        }
    }
}
