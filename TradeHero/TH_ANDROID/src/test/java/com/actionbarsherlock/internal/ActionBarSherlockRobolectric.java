package com.actionbarsherlock.internal;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuInflater;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * During initialization, {@link ActionBarSherlock} figures out which {@link com.actionbarsherlock.app.ActionBar} to use based on the API level. It
 * does this by checking the Build.Version.SDK_INT value which depends on the hidden <i>SystemProperties</i> class.
 *
 * Because Roboelectric does not have this, it always returns <code>0</code> for its API level causing {@link ActionBarSherlock} to crash. This class
 * helps resolve this issue by providing an {@link ActionBarSherlockNative} implementation for API level 0.
 *
 * @see ActionBarSherlock#registerImplementation(Class)
 */
@ActionBarSherlock.Implementation(api = 0)
public class ActionBarSherlockRobolectric extends ActionBarSherlockCompat
{
    private static final PrintStream FAKE_SYSTEM_ERR = new PrintStream(new PrintStream(new OutputStream()
    {
        public void write(int b)
        {
            // do nothing, since we don't want to log with this fake system err
        }
    }));

    final private ActionBar actionBar;

    public ActionBarSherlockRobolectric(Activity activity, int flags)
    {
        super(activity, flags);
        actionBar = new MockActionBar(activity);
    }

    @Override public void setContentView(int layoutResId)
    {
        // hide crazy system stack error trace, thrown by ActionBarSherlock when it could not find AndroidManifest.xml
        System.setErr(FAKE_SYSTEM_ERR);
        super.setContentView(layoutResId);
        System.setErr(System.err);
    }

    @Override public void setContentView(View view)
    {
        System.setErr(FAKE_SYSTEM_ERR);
        super.setContentView(view);
        System.setErr(System.err);
    }

    @Override public ActionBar getActionBar()
    {
        return actionBar;
    }

    @Override protected Context getThemedContext()
    {
        return mActivity;
    }

    @Override public MenuInflater getMenuInflater()
    {
        if (mMenuInflater == null)
        {
            mMenuInflater = new SherlockMenuInflater(mActivity);
        }
        return mMenuInflater;
    }
}