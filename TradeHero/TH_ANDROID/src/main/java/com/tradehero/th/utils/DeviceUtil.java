package com.tradehero.th.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import java.lang.ref.WeakReference;

public final class DeviceUtil
{
    public static final long DEFAULT_DELAY = 998;

    public static InputMethodManager getInputMethodManager(Context ctx)
    {
        return (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    // Attempt
    public static boolean isKeyboardVisible(Context ctx)
    {
        InputMethodManager imm = getInputMethodManager(ctx);
        return imm != null && imm.isAcceptingText();
    }

    public static void dismissKeyboard(Context ctx, View v)
    {
        InputMethodManager imm = getInputMethodManager(ctx);
        if (imm != null && v != null)
        {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static void dismissKeyboard(Activity activity)
    {
        if (activity != null)
        {
            dismissKeyboard(activity, activity.getCurrentFocus());
        }
    }

    public static void showKeyboardDelayed(View view)
    {
        showKeyboardDelayed(view, DEFAULT_DELAY);
    }

    public static void showKeyboardDelayed(View view, long delayMilliSec)
    {
        final WeakReference<View> viewRef = new WeakReference<>(view);
        view.postDelayed(new Runnable()
        {
            @Override public void run()
            {
                View viewToUse = viewRef.get();
                if (viewToUse != null)
                {
                    InputMethodManager inputManager = getInputMethodManager(viewToUse.getContext());
                    inputManager.showSoftInput(viewToUse, 0);
                }
            }
        }, delayMilliSec);
    }
}
