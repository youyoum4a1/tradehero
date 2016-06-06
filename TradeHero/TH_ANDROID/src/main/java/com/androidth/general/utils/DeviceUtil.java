package com.androidth.general.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import java.lang.ref.WeakReference;

public final class DeviceUtil
{
    public static final long DEFAULT_DELAY = 998;

    public static InputMethodManager getInputMethodManager(@NonNull Context ctx)
    {
        return (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    // Attempt
    public static boolean isKeyboardVisible(@NonNull Context ctx)
    {
        InputMethodManager imm = getInputMethodManager(ctx);
        return imm != null && imm.isAcceptingText();
    }

    public static void dismissKeyboard(@Nullable View v)
    {
        if (v != null)
        {
            InputMethodManager imm = getInputMethodManager(v.getContext());
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static void dismissKeyboard(@Nullable Activity activity)
    {
        if (activity != null)
        {
            dismissKeyboard(activity.getCurrentFocus());
        }
    }

    public static void showKeyboardDelayed(@NonNull View view)
    {
        showKeyboardDelayed(view, DEFAULT_DELAY);
    }

    public static void showKeyboardDelayed(@NonNull View view, long delayMilliSec)
    {
        final WeakReference<View> viewRef = new WeakReference<>(view);
        view.postDelayed(new Runnable()
        {
            @Override public void run()
            {
                View viewToUse = viewRef.get();
                if (viewToUse != null && viewToUse.isFocused())
                {
                    InputMethodManager inputManager = getInputMethodManager(viewToUse.getContext());
                    inputManager.showSoftInput(viewToUse, 0);
                }
            }
        }, delayMilliSec);
    }

    public static int getScreenWidth(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}
