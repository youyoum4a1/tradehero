package com.tradehero.th.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 11/19/13 Time: 4:38 PM To change this template use File | Settings | File Templates. */
public class AlertDialogUtil
{
    public static final String TAG = AlertDialogUtil.class.getSimpleName();

    public static void popWithCancelButton(final Context context, int titleResId, int descriptionResId, int cancelResId)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(titleResId)
                .setMessage(descriptionResId)
                .setIcon(R.drawable.th_app_logo)
                .setCancelable(true)
                .setNegativeButton(cancelResId, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
