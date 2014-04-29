package com.tradehero.common.utils;

import android.os.AsyncTask;

/**
 * This class helps create the illusion of the action taking a minimum time.
 * Can be used to give the system enough time to pop a message.
 * */
abstract public class SlowedAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
{
    public final long minimumMilliSeconds;

    public SlowedAsyncTask(long minimumMilliSeconds)
    {
        super();
        this.minimumMilliSeconds = minimumMilliSeconds;
    }

    abstract protected Result doBackgroundAction(Params... paramses);

    @Override protected final Result doInBackground(Params... paramses)
    {
        final long startTime = System.nanoTime();
        final Result result = doBackgroundAction(paramses);
        final long endTime = System.nanoTime();
        final long milliseconds = minimumMilliSeconds - Math.min (minimumMilliSeconds, (long) ((endTime - startTime) / 1000000f)); //for milliseconds
        try
        {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e)
        {
        }
        return result;
    }
}
