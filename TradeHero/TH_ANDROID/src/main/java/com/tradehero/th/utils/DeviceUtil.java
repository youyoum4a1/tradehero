/*
 ===========================================================================
 Copyright (c) 2012 Three Pillar Global Inc. http://threepillarglobal.com

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sub-license, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ===========================================================================
 */
package com.tradehero.th.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

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
