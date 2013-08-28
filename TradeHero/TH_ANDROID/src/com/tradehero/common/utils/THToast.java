package com.tradehero.common.utils;

import android.view.Gravity;
import android.widget.Toast;
import com.tradehero.th.base.Application;

/** Created with IntelliJ IDEA. User: tho Date: 8/19/13 Time: 12:33 PM Copyright (c) TradeHero */
public class THToast
{
    public static void show(String message)
    {
        Toast toast = Toast.makeText(Application.context(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    public static void show(int resourceId)
    {
        show(Application.getResourceString(resourceId));
    }
}
