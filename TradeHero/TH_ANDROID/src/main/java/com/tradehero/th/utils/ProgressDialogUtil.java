package com.tradehero.th.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Window;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/29/14 Time: 4:14 PM Copyright (c) TradeHero
 */
public class ProgressDialogUtil
{
    private static Map<Context, ProgressDialog> dialogs = new HashMap<>();

    @Inject public ProgressDialogUtil()
    {
        super();
    }

    public ProgressDialog create(Context context, String title, String message)
    {
        ProgressDialog dialog = dialogs.get(context);
        if (dialog == null)
        {
            dialog = ProgressDialog.show(context, title, message, true);
            dialogs.put(context, dialog);
        }
        else
        {
            if(TextUtils.isEmpty(title))
            {
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
            else {
                dialog.setTitle(title);
            }
            dialog.setMessage(message);
        }
        return dialog;
    }

    public ProgressDialog create(Context context, int titleResId, int messageResId)
    {
        return create(context, context.getString(titleResId), context.getString(messageResId));
    }

    public ProgressDialog show(Context context, String title, String message)
    {
        ProgressDialog dialog = create(context, title, message);
        dialog.show();
        return dialog;
    }

    public ProgressDialog show(Context context, int titleResId, int messageResId)
    {
        return show(context, context.getString(titleResId), context.getString(messageResId));
    }
}
