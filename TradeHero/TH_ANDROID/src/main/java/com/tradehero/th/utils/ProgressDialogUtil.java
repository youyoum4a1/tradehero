package com.tradehero.th.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.Window;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class ProgressDialogUtil
{
    @NonNull private static final Map<Context, WeakReference<ProgressDialog>> dialogs = new HashMap<>();

    @Nullable protected static ProgressDialog getDialog(@NonNull Context context)
    {
        WeakReference<ProgressDialog> weakDialog = dialogs.get(context);
        if (weakDialog != null)
        {
            return weakDialog.get();
        }
        return null;
    }

    @NonNull public static ProgressDialog create(@NonNull Context context, @Nullable String title, @Nullable String message)
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

    @NonNull public static ProgressDialog create(@NonNull Context context, @StringRes int titleResId, @StringRes int messageResId)
    {
        return create(context, context.getString(titleResId), context.getString(messageResId));
    }

    @NonNull public static ProgressDialog show(@NonNull Context context, @Nullable String title, @Nullable String message)
    {
        ProgressDialog dialog = create(context, title, message);
        dialog.show();
        return dialog;
    }

    @NonNull public static ProgressDialog show(@NonNull Context context, @StringRes int titleResId, @StringRes int messageResId)
    {
        return show(context, context.getString(titleResId), context.getString(messageResId));
    }

    @NonNull public static ProgressDialog create(@NonNull final Context context, @StringRes int content)
    {
        return create(context, context.getString(content));
    }

    @NonNull public static ProgressDialog create(@NonNull final Context context, @NonNull String content)
    {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(content);
        dialog.show();
        return dialog;
    }

    public static void dismiss(@NonNull Context context)
    {
        ProgressDialog dialog = getDialog(context);
        if (dialog != null)
        {
            dialog.dismiss();
        }
    }
}
