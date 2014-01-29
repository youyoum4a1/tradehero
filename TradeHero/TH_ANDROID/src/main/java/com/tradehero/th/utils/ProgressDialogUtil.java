package com.tradehero.th.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/29/14 Time: 4:14 PM Copyright (c) TradeHero
 */
public class ProgressDialogUtil
{
    private static Map<Context, ProgressDialog> dialogs = new HashMap<>();

    public static ProgressDialog create(Context context, String title, String message)
    {
        ProgressDialog dialog = dialogs.get(context);
        if (dialog == null)
        {
            dialog = ProgressDialog.show(context, title, message, true);
            dialogs.put(context, dialog);
        }
        else
        {
            dialog.setTitle(title);
            dialog.setMessage(message);
        }
        return dialog;
    }

    public static ProgressDialog show(Context context, String title, String message)
    {
        ProgressDialog dialog = create(context, title, message);
        dialog.show();
        return dialog;
    }

    public static ProgressDialog show(Context context, int titleResId, int messageResId)
    {
        return show(context, context.getString(titleResId), context.getString(messageResId));
    }
}
