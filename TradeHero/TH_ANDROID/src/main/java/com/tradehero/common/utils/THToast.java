package com.tradehero.common.utils;

import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import com.tradehero.th.base.Application;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.misc.exception.THException.ExceptionCode;

/** Created with IntelliJ IDEA. User: tho Date: 8/19/13 Time: 12:33 PM Copyright (c) TradeHero */
public class THToast
{
    private static final String TAG = THToast.class.getName();

    public static void show(String message)
    {
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            Toast toast = Toast.makeText(Application.context(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
            toast.show();
        }
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
