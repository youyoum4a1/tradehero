package com.tradehero.th.billing.googleplay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import com.tradehero.common.billing.googleplay.BaseIABProductDetail;
import com.tradehero.th.R;
import com.tradehero.th.billing.BillingAlertDialogUtil;
import com.tradehero.th.fragments.billing.googleplay.SKUDetailView;
import com.tradehero.th.fragments.billing.googleplay.SKUDetailsAdapter;
import com.tradehero.th.utils.ActivityUtil;
import com.tradehero.th.utils.VersionUtils;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/7/13 Time: 5:52 PM To change this template use File | Settings | File Templates. */
public class IABAlertDialogUtil extends BillingAlertDialogUtil
{
    public static final String TAG = IABAlertDialogUtil.class.getSimpleName();

    @Inject public ActivityUtil activityUtil;

    @Inject public IABAlertDialogUtil()
    {
        super();
    }

    @Override public void goToCreateAccount(final Context context)
    {
        Intent addAccountIntent = new Intent(Settings.ACTION_ADD_ACCOUNT);
        addAccountIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Still cannot get it to go back to TradeHero with back button
        context.startActivity(addAccountIntent);
    }

    public AlertDialog popWaitWhileLoading(final Context context)
    {
        return popWithNegativeButton(context, R.string.store_billing_loading_window_title,
                R.string.store_billing_loading_window_description,
                R.string.store_billing_loading_cancel);
    }

    public AlertDialog popVerificationFailed(final Context context)
    {
        return popWithNegativeButton(context, R.string.store_billing_verification_failed_window_title,
                R.string.store_billing_verification_failed_window_description,
                R.string.store_billing_verification_failed_cancel);
    }

    public AlertDialog popUserCancelled(final Context context)
    {
        return popWithOkCancelButton(context,
                R.string.store_billing_user_cancelled_window_title,
                R.string.store_billing_user_cancelled_window_description,
                R.string.store_billing_user_cancelled_ok,
                R.string.store_billing_user_cancelled_cancel,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                        sendSupportEmailCancelledPurchase(context);
                    }
                });
    }

    public void sendSupportEmailCancelledPurchase(final Context context)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(context, true);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "I cancelled the purchase");
        activityUtil.sendSupportEmail(context, emailIntent);
    }

    public AlertDialog popSKUAlreadyOwned(final Context context)
    {
        return popSKUAlreadyOwned(context, null);
    }

    public <SKUDetailsType extends BaseIABProductDetail>
        AlertDialog popSKUAlreadyOwned(final Context context, SKUDetailsType skuDetails)
    {
        return popWithOkCancelButton(context,
                skuDetails == null ?
                        context.getString(R.string.store_billing_sku_already_owned_window_title) :
                        String.format(context.getString(R.string.store_billing_sku_already_owned_name_window_title), skuDetails.description),
                context.getString(R.string.store_billing_sku_already_owned_window_description),
                R.string.store_billing_sku_already_owned_ok,
                R.string.store_billing_sku_already_owned_cancel,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                        sendSupportEmailPurchaseNotRestored(context);
                    }
                });
    }

    public void sendSupportEmailPurchaseNotRestored(final Context context)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(context, true);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My purchase is not being handled even after restart");
        activityUtil.sendSupportEmail(context, emailIntent);
    }

    public AlertDialog popBadResponse(final Context context)
    {
        return popWithNegativeButton(context, R.string.store_billing_bad_response_window_title,
                R.string.store_billing_bad_response_window_description,
                R.string.store_billing_bad_response_cancel);
    }

    public AlertDialog popRemoteError(final Context context)
    {
        return popWithNegativeButton(context, R.string.store_billing_remote_error_window_title,
                R.string.store_billing_remote_error_window_description,
                R.string.store_billing_remote_error_cancel);
    }

    public AlertDialog popSendIntent(final Context context)
    {
        return popWithNegativeButton(context, R.string.store_billing_send_intent_error_window_title,
                R.string.store_billing_send_intent_error_window_description,
                R.string.store_billing_send_intent_error_cancel);
    }

    public AlertDialog popFailedToReport(final Context context)
    {
        return popWithNegativeButton(context, R.string.store_billing_report_api_error_window_title,
                R.string.store_billing_report_api_error_window_description,
                R.string.store_billing_report_api_error_cancel);
    }

    public AlertDialog popFailedToLoadRequiredInfo(final Context context)
    {
        return popWithNegativeButton(context, R.string.store_billing_load_info_error_window_title,
                R.string.store_billing_load_info_error_window_description,
                R.string.store_billing_load_info_error_cancel);
    }

    public AlertDialog popUnknownError(final Context context)
    {
        return popWithOkCancelButton(context,
                R.string.store_billing_unknown_error_window_title,
                R.string.store_billing_unknown_error_window_description,
                R.string.store_billing_unknown_error_ok,
                R.string.store_billing_unknown_error_cancel,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // TODO open email
                        dialog.cancel();
                    }
                });
    }

    public void sendSupportEmailBillingUnknownError(final Context context, final Exception exception)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(VersionUtils.getExceptionStringsAndTraceParameters(context, exception));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "There was an unidentified error");
        activityUtil.sendSupportEmail(context, emailIntent);
    }

    public <SKUDetailsType extends BaseIABProductDetail, SKUDetailViewType extends SKUDetailView<SKUDetailsType>>
    AlertDialog popBuyDialog(
            final Context context,
            final SKUDetailsAdapter<SKUDetailsType, SKUDetailViewType> detailsAdapter,
            int titleResId,
            final OnDialogSKUDetailsClickListener<SKUDetailsType> clickListener,
            final Runnable runOnPurchaseComplete)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(titleResId)
                .setIcon(R.drawable.th_app_logo)
                .setSingleChoiceItems(detailsAdapter, 0, new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (clickListener != null)
                        {
                            clickListener.onDialogSKUDetailsClicked(dialogInterface, i, (SKUDetailsType) detailsAdapter.getItem(i), runOnPurchaseComplete);
                        }
                        dialogInterface.cancel();
                    }
                })
                .setCancelable(true);
                //.setNegativeButton(R.string.store_buy_virtual_dollar_window_button_cancel, new DialogInterface.OnClickListener()
                //{
                //    public void onClick(DialogInterface dialog, int id)
                //    {
                //        dialog.cancel();
                //    }
                //});
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
        return alertDialog;
    }

    public AlertDialog popSendEmailSupportReportFailed(final Context context, final DialogInterface.OnClickListener okClickListener)
    {
        return popWithOkCancelButton(context,
                R.string.google_play_send_support_email_report_fail_title,
                R.string.google_play_send_support_email_report_fail_message,
                R.string.google_play_send_support_email_report_fail_ok,
                R.string.google_play_send_support_email_report_fail_cancel,
                okClickListener);
    }

    public AlertDialog popOfferSendEmailSupportConsumeFailed(final Context context, final Exception exception)
    {
        return popSendEmailSupportConsumeFailed(context, new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                sendSupportEmailConsumeFailed(context, exception);
            }
        });
    }

    public void sendSupportEmailConsumeFailed(final Context context, Exception exception)
    {
        activityUtil.sendSupportEmail(context, GooglePlayUtils.getSupportPurchaseConsumeEmailIntent(context, exception));
    }

    public AlertDialog popSendEmailSupportConsumeFailed(final Context context, final DialogInterface.OnClickListener okClickListener)
    {
        return popWithOkCancelButton(context,
                R.string.google_play_send_support_email_consume_fail_title,
                R.string.google_play_send_support_email_consume_fail_message,
                R.string.google_play_send_support_email_consume_fail_ok,
                R.string.google_play_send_support_email_consume_fail_cancel,
                okClickListener);
    }

    public static interface OnDialogSKUDetailsClickListener<BaseIABProductDetailsType extends BaseIABProductDetail>
    {
        void onDialogSKUDetailsClicked(DialogInterface dialogInterface, int position, BaseIABProductDetailsType skuDetails, Runnable runOnPurchaseComplete);
    }
}
