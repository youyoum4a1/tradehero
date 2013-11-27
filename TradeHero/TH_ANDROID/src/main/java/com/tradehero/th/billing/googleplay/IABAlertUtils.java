package com.tradehero.th.billing.googleplay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import com.tradehero.common.billing.googleplay.BaseIABProductDetails;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.fragments.billing.SKUDetailsAdapter;
import com.tradehero.th.fragments.billing.SKUDetailView;
import com.tradehero.th.utils.AlertDialogUtil;

/** Created with IntelliJ IDEA. User: xavier Date: 11/7/13 Time: 5:52 PM To change this template use File | Settings | File Templates. */
public class IABAlertUtils
{
    public static final String TAG = IABAlertUtils.class.getSimpleName();

    public static void popBillingUnavailable(final Context context)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(R.string.store_billing_unavailable_window_title)
                .setMessage(R.string.store_billing_unavailable_window_description)
                .setIcon(R.drawable.google_play_store)
                .setCancelable(true)
                .setNegativeButton(R.string.store_billing_unavailable_cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.store_billing_unavailable_act, new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Intent addAccountIntent = new Intent(Settings.ACTION_ADD_ACCOUNT);
                        addAccountIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Still cannot get it to go back to TradeHero with back button
                        context.startActivity(addAccountIntent);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void popWaitWhileLoading(final Context context)
    {
        AlertDialogUtil.popWithCancelButton(context, R.string.store_billing_loading_window_title,
                R.string.store_billing_loading_window_description,
                R.string.store_billing_loading_cancel);
    }

    public static void popVerificationFailed(final Context context)
    {
        AlertDialogUtil.popWithCancelButton(context, R.string.store_billing_verification_failed_window_title,
                R.string.store_billing_verification_failed_window_description,
                R.string.store_billing_verification_failed_cancel);
    }

    public static void popUserCancelled(final Context context)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(R.string.store_billing_user_cancelled_window_title)
                .setMessage(R.string.store_billing_user_cancelled_window_description)
                .setCancelable(true)
                .setPositiveButton(R.string.store_billing_user_cancelled_ok, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // TODO open email
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.store_billing_user_cancelled_cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void popSKUAlreadyOwned(final Context context)
    {
        popSKUAlreadyOwned(context, null);
    }

    public static <SKUDetailsType extends BaseIABProductDetails>void popSKUAlreadyOwned(final Context context, SKUDetailsType skuDetails)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(skuDetails == null ?
                        context.getString(R.string.store_billing_sku_already_owned_window_title) :
                        String.format(context.getString(R.string.store_billing_sku_already_owned_name_window_title), skuDetails.description))
                .setMessage(R.string.store_billing_sku_already_owned_window_description)
                .setCancelable(true)
                .setPositiveButton(R.string.store_billing_sku_already_owned_ok, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // TODO open email
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.store_billing_sku_already_owned_cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void popBadResponse(final Context context)
    {
        AlertDialogUtil.popWithCancelButton(context, R.string.store_billing_bad_response_window_title,
                R.string.store_billing_bad_response_window_description,
                R.string.store_billing_bad_response_cancel);
    }

    public static void popRemoteError(final Context context)
    {
        AlertDialogUtil.popWithCancelButton(context, R.string.store_billing_remote_error_window_title,
                R.string.store_billing_remote_error_window_description,
                R.string.store_billing_remote_error_cancel);
    }

    public static void popSendIntent(final Context context)
    {
        AlertDialogUtil.popWithCancelButton(context, R.string.store_billing_send_intent_error_window_title,
                R.string.store_billing_send_intent_error_window_description,
                R.string.store_billing_send_intent_error_cancel);
    }

    public static void popFailedToReport(final Context context)
    {
        AlertDialogUtil.popWithCancelButton(context, R.string.store_billing_report_api_error_window_title,
                R.string.store_billing_report_api_error_window_description,
                R.string.store_billing_report_api_error_cancel);
    }

    public static void popFailedToLoadRequiredInfo(final Context context)
    {
        AlertDialogUtil.popWithCancelButton(context, R.string.store_billing_load_info_error_window_title,
                R.string.store_billing_load_info_error_window_description,
                R.string.store_billing_load_info_error_cancel);
    }

    public static void popUnknownError(final Context context)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(R.string.store_billing_unknown_error_window_title)
                .setMessage(R.string.store_billing_unknown_error_window_description)
                .setCancelable(true)
                .setPositiveButton(R.string.store_billing_unknown_error_ok, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // TODO open email
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.store_billing_unknown_error_cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static <SKUDetailsType extends BaseIABProductDetails, SKUDetailViewType extends SKUDetailView<SKUDetailsType>> void popBuyDialog(
        final Context context, final SKUDetailsAdapter<SKUDetailsType, SKUDetailViewType> detailsAdapter,
        int titleResId, final OnDialogSKUDetailsClickListener<SKUDetailsType> clickListener)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set dialog message
        alertDialogBuilder
                .setTitle(titleResId)
                .setIcon(R.drawable.th_app_logo)
                .setSingleChoiceItems(detailsAdapter, 0, new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (clickListener != null)
                        {
                            clickListener.onDialogSKUDetailsClicked(dialogInterface, i, (SKUDetailsType) detailsAdapter.getItem(i));
                        }
                        dialogInterface.cancel();
                    }
                })
                .setCancelable(true)
                .setNegativeButton(R.string.store_buy_virtual_dollar_window_button_cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static interface OnDialogSKUDetailsClickListener<SKUDetailsType extends BaseIABProductDetails>
    {
        void onDialogSKUDetailsClicked(DialogInterface dialogInterface, int position, SKUDetailsType skuDetails);
    }

    public static void popSendEmailSupportReportFailed(final Context context, final DialogInterface.OnClickListener clickListener)
    {
        AlertDialogUtil.popWithOkCancelButton(context,
                R.string.google_play_send_support_email_report_fail_title,
                R.string.google_play_send_support_email_report_fail_message,
                R.string.google_play_send_support_email_report_fail_ok,
                R.string.google_play_send_support_email_report_fail_cancel,
                clickListener);
    }

    public static void popOfferSendEmailSupportConsumeFailed(final Context context, final Exception exception)
    {
        // Offer to send an email to support
        THLog.e(TAG, "Could not consume a purchase", exception);

        IABAlertUtils.popSendEmailSupportConsumeFailed(context, new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                sendSupportEmailConsumeFailed(context, exception);
            }
        });
    }

    public static void sendSupportEmailConsumeFailed(final Context context, Exception exception)
    {
        context.startActivity(Intent.createChooser(
                GooglePlayUtils.getSupportPurchaseConsumeEmailIntent(context, exception),
                context.getString(R.string.google_play_send_support_email_chooser_title)));
    }

    public static void popSendEmailSupportConsumeFailed(final Context context, final DialogInterface.OnClickListener clickListener)
    {
        AlertDialogUtil.popWithOkCancelButton(context,
                R.string.google_play_send_support_email_consume_fail_title,
                R.string.google_play_send_support_email_consume_fail_message,
                R.string.google_play_send_support_email_consume_fail_ok,
                R.string.google_play_send_support_email_consume_fail_cancel,
                clickListener);
    }

    public static void popConsumePurchaseSuccess(final Context context, final String skuName)
    {
        AlertDialogUtil.popWithCancelButton(context,
                context.getString(R.string.google_play_consumed_purchase_title),
                skuName == null ? context.getString(R.string.google_play_consumed_purchase_message) :
                    String.format(context.getString(R.string.google_play_consumed_purchase_message_info), skuName),
                context.getString(R.string.google_play_consumed_purchase_cancel));
    }
}
