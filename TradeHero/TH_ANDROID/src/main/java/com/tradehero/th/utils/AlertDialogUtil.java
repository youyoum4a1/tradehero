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

    public static void popWithCancelButton(final Context context, String titleRes, String descriptionRes, String cancelRes)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(titleRes)
                .setMessage(descriptionRes)
                .setIcon(R.drawable.th_app_logo)
                .setCancelable(true)
                .setNegativeButton(cancelRes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void popWithOkCancelButton(final Context context, int titleResId, int descriptionResId, int okResId, int cancelResId, final DialogInterface.OnClickListener okClickListener)
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
                }).setPositiveButton(okResId, new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialog, int which)
                    {
                        if (okClickListener != null)
                        {
                            okClickListener.onClick(dialog, which);
                        }
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
