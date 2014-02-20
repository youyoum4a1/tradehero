package com.tradehero.common.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import com.tradehero.th.misc.exception.THException;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: tho Date: 8/19/13 Time: 12:33 PM Copyright (c) TradeHero */
public class THToast
{
    public static Handler toastHandler = null;
    public static int toastPosition = Application.context().getResources().getDimensionPixelOffset(R.dimen.abs__action_bar_default_height);
    public static void show(final String message)
    {
        //THLog.e(TAG, "show " + message, new IllegalArgumentException());
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            toastOnUIThread(message);
        }
        else
        {
            if (toastHandler == null)
            {
                toastHandler = new Handler(Looper.getMainLooper());
            }

            toastHandler.post(new Runnable()
            {
                @Override public void run()
                {
                    toastOnUIThread(message);
                }
            });
            Timber.d("Problem: Toast is called from background thread: %s", message);
        }
    }

    private static void toastOnUIThread(String message)
    {
        Toast toast = Toast.makeText(Application.context(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, toastPosition);
        toast.show();
    }

    public static void show(int resourceId)
    {
        show(Application.getResourceString(resourceId));
    }

    public static void show(THException ex)
    {
        show(ex.getMessage());
    }

    /**
     * Helps work around the fact that we may want to toast from other threads.
     * @param view
     * @param message
     */
    public static void post(View view, final String message)
    {
        if (view != null)
        {
            view.post(new Runnable()
            {
                @Override public void run()
                {
                    THToast.show(message);
                }
            });
        }
    }

    public static void post(View view, final int resourceId)
    {
        post(view, Application.getResourceString(resourceId));
    }

    public static void post(View view, final THException ex)
    {
        post(view, ex.getMessage());
    }
}
