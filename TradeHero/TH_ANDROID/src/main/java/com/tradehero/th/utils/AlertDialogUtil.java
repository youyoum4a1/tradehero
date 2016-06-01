package com.ayondo.academy.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.ayondo.academy.R;

public class AlertDialogUtil
{
    private static ProgressDialog mProgressDialog;

    @NonNull
    public static Dialog popTutorialContent(
            @NonNull final Context context,
            int layoutResourceId)
    {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.tutorial_master_layout);
        ViewGroup tutorialContentView = (ViewGroup) dialog.findViewById(R.id.tutorial_content);
        LayoutInflater.from(context).inflate(layoutResourceId, tutorialContentView, true);
        ((View) tutorialContentView.getParent()).setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.95f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow()
                .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
        return dialog;
    }

    public static void showProgressDialog(@NonNull final Context context, @Nullable String content)
    {
        if (mProgressDialog != null)
        {
            mProgressDialog.dismiss();
        }
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(content);
        mProgressDialog.show();
    }

    public static void dismissProgressDialog()
    {
        ProgressDialog progressDialogCopy = mProgressDialog;
        if (progressDialogCopy != null)
        {
            progressDialogCopy.dismiss();
        }
        mProgressDialog = null;
    }
}
