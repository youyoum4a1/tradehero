package com.ayondo.academy.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.Window;

public class ProgressDialogUtil
{
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
}
