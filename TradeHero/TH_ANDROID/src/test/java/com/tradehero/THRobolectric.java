package com.tradehero;

import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

public class THRobolectric extends Robolectric
{
    // HACK Let's see if we still have random results on AsyncTask-based tests.
    public static void runBgUiTasks(int count) throws InterruptedException
    {
        assertThat(count).isGreaterThan(0);
        for (int i = 0; i < count; i++)
        {
            Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
            Thread.sleep(50);
            Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
        }
    }
}
