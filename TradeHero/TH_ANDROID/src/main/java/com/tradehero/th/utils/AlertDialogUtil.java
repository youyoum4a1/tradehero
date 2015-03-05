package com.tradehero.th.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import android.widget.ListAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.UserServiceWrapper;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public class AlertDialogUtil
{
    private ProgressDialog mProgressDialog;

    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;

    //<editor-fold desc="Constructors">
    @Inject public AlertDialogUtil()
    {
        super();
    }
    //</editor-fold>

    @NotNull
    public DialogInterface.OnClickListener createDefaultCancelListener()
    {
        return new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        };
    }

    @NotNull
    public AlertDialog popWithNegativeButton(
            @NotNull final Context context,
            int titleResId, int descriptionResId,
            int cancelResId)
    {
        return popWithNegativeButton(context, titleResId, descriptionResId, cancelResId,
                createDefaultCancelListener());
    }

    @NotNull
    public AlertDialog popWithNegativeButton(
            @NotNull final Context context,
            int titleResId, int descriptionResId,
            int cancelResId,
            @Nullable DialogInterface.OnClickListener cancelListener)
    {
        return popWithNegativeButton(context,
                context.getString(titleResId),
                context.getString(descriptionResId),
                context.getString(cancelResId),
                cancelListener);
    }

    @NotNull
    public AlertDialog popWithNegativeButton(
            @NotNull final Context context,
            @Nullable String titleRes, @Nullable String descriptionRes,
            @NotNull String cancelRes)
    {
        return popWithNegativeButton(context, titleRes, descriptionRes, cancelRes,
                createDefaultCancelListener());
    }

    @NotNull
    public AlertDialog popWithNegativeButton(
            @NotNull final Context context,
            @Nullable String titleRes, @Nullable String descriptionRes,
            @NotNull String cancelRes,
            @Nullable DialogInterface.OnClickListener cancelListener)
    {
        return popWithNegativeButton(context, titleRes,
                descriptionRes, cancelRes,
                null, null,
                cancelListener);
    }

    @NotNull
    public AlertDialog popWithNegativeButton(
            @NotNull final Context context,
            @Nullable String titleRes, @Nullable String descriptionRes,
            @NotNull String cancelRes,
            @Nullable final ListAdapter detailsAdapter,
            @Nullable final OnClickListener adapterListener,
            @Nullable DialogInterface.OnClickListener cancelListener)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setCancelable(true)
                .setNegativeButton(cancelRes, cancelListener);
        if (titleRes != null)
        {
            alertDialogBuilder.setTitle(titleRes);
        }
        if (descriptionRes != null)
        {
            alertDialogBuilder.setMessage(descriptionRes);
        }
        if (detailsAdapter != null)
        {
            alertDialogBuilder
                    .setSingleChoiceItems(detailsAdapter, 0, new DialogInterface.OnClickListener()
                    {
                        @SuppressWarnings("unchecked")
                        @Override public void onClick(DialogInterface dialogInterface, int i)
                        {
                            if (adapterListener != null)
                            {
                                adapterListener.onClick(detailsAdapter.getItem(i));
                            }
                            dialogInterface.cancel();
                        }
                    });
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
        return alertDialog;
    }

    @NotNull
    public AlertDialog popWithOkCancelButton(
            @NotNull final Context context,
            int titleResId, int descriptionResId,
            int okResId, int cancelResId,
            @Nullable final DialogInterface.OnClickListener okClickListener)
    {
        return popWithOkCancelButton(context, titleResId, descriptionResId, okResId, cancelResId,
                okClickListener, createDefaultCancelListener());
    }

    @NotNull
    public AlertDialog popWithOkCancelButton(
            @NotNull final Context context,
            int titleResId, int descriptionResId,
            int okResId, int cancelResId,
            @Nullable final DialogInterface.OnClickListener okClickListener,
            @Nullable final DialogInterface.OnClickListener cancelClickListener)
    {
        return popWithOkCancelButton(context,
                context.getString(titleResId),
                context.getString(descriptionResId),
                okResId,
                cancelResId,
                okClickListener,
                cancelClickListener);
    }

    @NotNull
    public AlertDialog popWithOkCancelButton(
            @NotNull final Context context,
            @NotNull String title, @NotNull String description,
            int okResId, int cancelResId,
            @Nullable final DialogInterface.OnClickListener okClickListener)
    {
        return popWithOkCancelButton(context, title, description, okResId, cancelResId,
                okClickListener, createDefaultCancelListener());
    }

    @NotNull
    public AlertDialog popWithOkCancelButton(
            @NotNull final Context context,
            @NotNull String title, @NotNull String description,
            int okResId, int cancelResId,
            @Nullable final DialogInterface.OnClickListener okClickListener,
            @Nullable final DialogInterface.OnClickListener cancelClickListener)
    {
        return popWithOkCancelButton(context, title, description, okResId, cancelResId,
                okClickListener, cancelClickListener, null);
    }

    @NotNull
    public AlertDialog popWithOkCancelButton(
            @NotNull final Context context,
            @NotNull String title, @NotNull String description,
            int okResId, int cancelResId,
            @Nullable final DialogInterface.OnClickListener okClickListener,
            @Nullable final DialogInterface.OnClickListener cancelClickListener,
            @Nullable final DialogInterface.OnDismissListener onDismissListener)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(title)
                .setMessage(description)
                .setCancelable(true)
                .setNegativeButton(cancelResId, cancelClickListener)
                .setPositiveButton(okResId, okClickListener);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnDismissListener(onDismissListener);
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
        return alertDialog;
    }

    @NotNull
    public AlertDialog popMarketClosed(
            @NotNull final Context context,
            @Nullable SecurityId securityId)
    {
        if (securityId == null)
        {
            return popWithNegativeButton(context,
                    R.string.alert_dialog_market_close_title,
                    R.string.alert_dialog_market_close_message_basic,
                    R.string.alert_dialog_market_close_cancel);
        }
        else
        {
            return popWithNegativeButton(context,
                    context.getString(R.string.alert_dialog_market_close_title),
                    context.getString(R.string.alert_dialog_market_close_message,
                            securityId.getExchange(),
                            securityId.getSecuritySymbol()),
                    context.getString(R.string.alert_dialog_market_close_cancel));
        }
    }

    public void showProgressDialog(final Context context, @Nullable String content)
    {
            if (context == null)
            {
                return;
            }
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

    public void dismissProgressDialog()
    {
        if (mProgressDialog != null)
        {
            mProgressDialog.dismiss();
        }
    }

    public static interface OnClickListener<DTOType>
    {
        void onClick(DTOType which);
    }
}
