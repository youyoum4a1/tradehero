package org.robolectric.shadows;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.util.Join;
import org.robolectric.util.Transcript;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AsyncTaskTest
{
    private Transcript transcript;

    @Before
    public void setUp() throws Exception
    {
        transcript = new Transcript();
        Robolectric.getBackgroundThreadScheduler().pause();
        Robolectric.getForegroundThreadScheduler().pause();
    }

    @Test
    public void testNormalFlow() throws Exception
    {
        AsyncTask<String, String, String> asyncTask = new MyAsyncTask();

        asyncTask.execute("a", "b");
        transcript.assertEventsSoFar("onPreExecute");

        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        transcript.assertEventsSoFar("doInBackground a, b");
        assertEquals("Result should get stored in the AsyncTask", "c", asyncTask.get(100, TimeUnit.MILLISECONDS));

        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
        transcript.assertEventsSoFar("onPostExecute c");
    }

    @Test
    public void testCancelBeforeBackground() throws Exception
    {
        AsyncTask<String, String, String> asyncTask = new MyAsyncTask();

        asyncTask.execute("a", "b");
        transcript.assertEventsSoFar("onPreExecute");

        assertTrue(asyncTask.cancel(true));
        assertTrue(asyncTask.isCancelled());

        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        transcript.assertNoEventsSoFar();

        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
        transcript.assertEventsSoFar("onCancelled");
    }

    @Test
    public void testCancelBeforePostExecute() throws Exception
    {
        AsyncTask<String, String, String> asyncTask = new MyAsyncTask();

        asyncTask.execute("a", "b");
        transcript.assertEventsSoFar("onPreExecute");

        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        transcript.assertEventsSoFar("doInBackground a, b");
        assertEquals("Result should get stored in the AsyncTask", "c", asyncTask.get(100, TimeUnit.MILLISECONDS));

        assertFalse(asyncTask.cancel(true));
        assertFalse(asyncTask.isCancelled());

        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
        transcript.assertEventsSoFar("onPostExecute c");
    }

    @Test
    public void progressUpdatesAreQueuedUntilBackgroundThreadFinishes() throws Exception
    {
        AsyncTask<String, String, String> asyncTask = new MyAsyncTask()
        {
            @Override
            protected String doInBackground(String... strings)
            {
                publishProgress("33%");
                publishProgress("66%");
                publishProgress("99%");
                return "done";
            }
        };

        asyncTask.execute("a", "b");
        transcript.assertEventsSoFar("onPreExecute");

        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        transcript.assertNoEventsSoFar();
        assertEquals("Result should get stored in the AsyncTask", "done", asyncTask.get(100, TimeUnit.MILLISECONDS));

        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
        transcript.assertEventsSoFar(
                "onProgressUpdate 33%",
                "onProgressUpdate 66%",
                "onProgressUpdate 99%",
                "onPostExecute done");
    }

    @Test
    public void executeReturnsAsyncTask() throws Exception
    {
        Robolectric.getBackgroundThreadScheduler().unPause();
        AsyncTask<String, String, String> asyncTask = new MyAsyncTask();
        assertThat(asyncTask.execute("a", "b").get()).isEqualTo("c");
    }

    @Test
    public void shouldGetStatusForAsyncTask() throws Exception
    {
        AsyncTask<String, String, String> asyncTask = new MyAsyncTask();
        assertThat(asyncTask.getStatus()).isEqualTo(AsyncTask.Status.PENDING);
        asyncTask.execute("a");
        assertThat(asyncTask.getStatus()).isEqualTo(AsyncTask.Status.RUNNING);
        Robolectric.getBackgroundThreadScheduler().unPause();
        assertThat(asyncTask.getStatus()).isEqualTo(AsyncTask.Status.FINISHED);
    }

    @Test
    public void onPostExecute_doesNotSwallowExceptions() throws Exception
    {
        Robolectric.getBackgroundThreadScheduler().unPause();
        Robolectric.getForegroundThreadScheduler().unPause();

        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                throw new RuntimeException("Don't swallow me!");
            }
        };

        try
        {
            asyncTask.execute();
            fail("Task swallowed onPostExecute() exception!");
        } catch (RuntimeException e)
        {
            assertThat(e.getCause().getMessage()).isEqualTo("java.lang.RuntimeException: Don't swallow me!");
        }
    }

    @Test
    public void executeOnExecutor_usesPassedExecutor() throws Exception
    {
        AsyncTask<String, String, String> asyncTask = new MyAsyncTask();

        assertThat(asyncTask.getStatus()).isEqualTo(AsyncTask.Status.PENDING);

        asyncTask.executeOnExecutor(new ImmediateExecutor(), "a", "b");

        assertThat(asyncTask.getStatus()).isEqualTo(AsyncTask.Status.FINISHED);
        transcript.assertEventsSoFar("onPreExecute", "doInBackground a, b");
        assertEquals("Result should get stored in the AsyncTask", "c", asyncTask.get());

        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
        transcript.assertEventsSoFar("onPostExecute c");
    }

    private class MyAsyncTask extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            transcript.add("onPreExecute");
        }

        @Override
        protected String doInBackground(String... strings)
        {
            transcript.add("doInBackground " + Join.join(", ", (Object[]) strings));
            return "c";
        }

        @Override
        protected void onProgressUpdate(String... values)
        {
            transcript.add("onProgressUpdate " + Join.join(", ", (Object[]) values));
        }

        @Override
        protected void onPostExecute(String s)
        {
            transcript.add("onPostExecute " + s);
        }

        @Override
        protected void onCancelled()
        {
            transcript.add("onCancelled");
        }
    }

    public class ImmediateExecutor implements Executor
    {
        @Override
        public void execute(@NonNull Runnable command)
        {
            command.run();
        }
    }
}