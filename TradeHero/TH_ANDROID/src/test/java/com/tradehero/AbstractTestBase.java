package com.tradehero;

import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

abstract public class AbstractTestBase
{
    // HACK Let's see if we still have random results on AsyncTask-based tests.
    protected void runBgUiTasks(int count) throws InterruptedException
    {
        assertThat(count).isGreaterThan(0);
        for (int i = 0; i < count; i++)
        {
            Robolectric.runBackgroundTasks();
            Thread.sleep(50);
            Robolectric.runUiThreadTasks();
            Robolectric.runUiThreadTasksIncludingDelayedTasks();
        }
    }
}
