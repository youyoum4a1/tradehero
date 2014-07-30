package com.tradehero.th.utils;

import android.app.ProgressDialog;
import android.content.Context;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProgressDialogUtil
{
    private static final Map<Context, WeakReference<ProgressDialog>> dialogs = new HashMap<>();

    //<editor-fold desc="Constructors">
    @Inject public ProgressDialogUtil()
    {
        super();
    }
    //</editor-fold>

    @Nullable protected ProgressDialog getDialog(@NotNull Context context)
    {
        WeakReference<ProgressDialog> weakDialog = dialogs.get(context);
        if (weakDialog != null)
        {
            return weakDialog.get();
        }
        return null;
    }

    @NotNull public ProgressDialog create(@NotNull Context context, @Nullable String title, @Nullable String message)
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

    @NotNull public ProgressDialog create(@NotNull Context context, int titleResId, int messageResId)
    {
        return create(context, context.getString(titleResId), context.getString(messageResId));
    }

    @NotNull public ProgressDialog show(@NotNull Context context, @Nullable String title, @Nullable String message)
    {
        ProgressDialog dialog = create(context, title, message);
        dialog.show();
        return dialog;
    }

    @NotNull public ProgressDialog show(@NotNull Context context, int titleResId, int messageResId)
    {
        return show(context, context.getString(titleResId), context.getString(messageResId));
    }

    public void dismiss(@NotNull Context context)
    {
        ProgressDialog dialog = getDialog(context);
        if (dialog != null)
        {
            dialog.dismiss();
        }
    }
}
