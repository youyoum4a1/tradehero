package com.tradehero.common.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import com.ayondo.academy.R;
import com.ayondo.academy.base.THApp;
import com.ayondo.academy.misc.exception.THException;
import timber.log.Timber;

public class THToast
{
    public static Handler toastHandler = null;
    public static int toastPosition = THApp.context().getResources().getDimensionPixelOffset(R.dimen.button_height_small);
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
        if (message != null)
        {
            Toast toast = Toast.makeText(THApp.context(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, toastPosition);
            toast.show();
        }
    }

    public static void show(int resourceId)
    {
        show(THApp.context().getString(resourceId));
    }

    public static void show(THException ex)
    {
        String message = ex.getMessage();
        if (message == null)
        {
            show(R.string.error_unknown);
        }
        else
        {
            show(message);
        }
    }

    /**
     * Helps work around the fact that we may want to toast from other threads.
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
        post(view, THApp.context().getString(resourceId));
    }

    public static void post(View view, final THException ex)
    {
        post(view, ex.getMessage());
    }
}
