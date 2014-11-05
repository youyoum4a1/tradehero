package com.tradehero.th.utils;

import android.app.ProgressDialog;
import android.content.Context;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ProgressDialogUtil
{
    private static final Map<Context, WeakReference<ProgressDialog>> dialogs = new HashMap<>();

    //<editor-fold desc="Constructors">
    @Inject public ProgressDialogUtil()
    {
        super();
    }
    //</editor-fold>

    @Nullable protected ProgressDialog getDialog(@NonNull Context context)
    {
        WeakReference<ProgressDialog> weakDialog = dialogs.get(context);
        if (weakDialog != null)
        {
            return weakDialog.get();
        }
        return null;
    }

    @NonNull public ProgressDialog create(@NonNull Context context, @Nullable String title, @Nullable String message)
    {
        ProgressDialog dialog = getDialog(context);
        if (dialog == null)
        {
            dialog = ProgressDialog.show(context, title, message, true);
            dialogs.put(context, new WeakReference<>(dialog));
        }
        else
        {
            dialog.setTitle(title);
            dialog.setMessage(message);
        }

        return dialog;
    }

    @NonNull public ProgressDialog create(@NonNull Context context, int titleResId, int messageResId)
    {
        return create(context, context.getString(titleResId), context.getString(messageResId));
    }

    @NonNull public ProgressDialog show(@NonNull Context context, @Nullable String title, @Nullable String message)
    {
        ProgressDialog dialog = create(context, title, message);
        dialog.show();
        return dialog;
    }

    @NonNull public ProgressDialog show(@NonNull Context context, int titleResId, int messageResId)
    {
        return show(context, context.getString(titleResId), context.getString(messageResId));
    }

    public void dismiss(@NonNull Context context)
    {
        ProgressDialog dialog = getDialog(context);
        if (dialog != null)
        {
            dialog.dismiss();
        }
    }
}
