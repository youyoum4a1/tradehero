package com.tradehero.th.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListAdapter;
import com.tradehero.thm.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.fragments.social.FollowDialogView;
import com.tradehero.th.models.social.FollowDialogCombo;
import com.tradehero.th.models.social.OnFollowRequestedListener;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AlertDialogUtil
{
    private ProgressDialog mProgressDialog;

    @Inject public AlertDialogUtil()
    {
        super();
    }

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
                .setIcon(R.drawable.th_app_logo)
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(title)
                .setMessage(description)
                .setIcon(R.drawable.th_app_logo)
                .setCancelable(true)
                .setNegativeButton(cancelResId, cancelClickListener)
                .setPositiveButton(okResId, okClickListener);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
        return alertDialog;
    }

    @NotNull
    public Dialog popTutorialContent(
            @NotNull final Context context,
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
                    String.format(context.getString(R.string.alert_dialog_market_close_message),
                            securityId.getExchange(), securityId.getSecuritySymbol()),
                    context.getString(R.string.alert_dialog_market_close_cancel));
        }
    }

    public FollowDialogCombo showFollowDialog(
            @NotNull final Context context,
            @Nullable UserBaseDTO userBaseDTO,
            final int followType,
            @NotNull final OnFollowRequestedListener followRequestedListener)
    {
        if (followType == UserProfileDTOUtil.IS_PREMIUM_FOLLOWER)
        {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        FollowDialogView followDialogView = (FollowDialogView) inflater.inflate(R.layout.follow_dialog, null);
        followDialogView.setFollowType(followType);
        followDialogView.display(userBaseDTO);

        builder.setView(followDialogView);
        builder.setCancelable(true);

        final AlertDialog mFollowDialog = builder.create();
        mFollowDialog.show();

        followDialogView.setFollowRequestedListener(new OnFollowRequestedListener()
        {
            @Override public void freeFollowRequested(UserBaseKey heroId)
            {
                onFinish();
                if (followType != UserProfileDTOUtil.IS_FREE_FOLLOWER)
                {
                    followRequestedListener.freeFollowRequested(heroId);
                }
            }

            @Override public void premiumFollowRequested(UserBaseKey heroId)
            {
                onFinish();
                followRequestedListener.premiumFollowRequested(heroId);
            }

            private void onFinish()
            {
                mFollowDialog.dismiss();
            }
        });

        return new FollowDialogCombo(mFollowDialog, followDialogView);
    }

    public void showProgressDialog(@NotNull final Context context)
    {
        if (mProgressDialog != null)
        {
            mProgressDialog.dismiss();
        }
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
    }

    public void showProgressDialog(@NotNull final Context context, @Nullable String content)
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
