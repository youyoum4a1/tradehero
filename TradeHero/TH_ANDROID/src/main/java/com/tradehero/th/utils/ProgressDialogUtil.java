package com.tradehero.th.utils;

import android.app.ProgressDialog;
import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

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
            dialog.setTitle(title);
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

    public void dismiss(Context context)
    {
        ProgressDialog dialog = dialogs.get(context);
        if (dialog != null)
        {
            dialog.dismiss();
        }
    }
}
