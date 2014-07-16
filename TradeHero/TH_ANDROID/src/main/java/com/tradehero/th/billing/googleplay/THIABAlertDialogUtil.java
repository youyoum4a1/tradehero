package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.googleplay.BaseIABProductDetail;
import com.tradehero.common.billing.googleplay.BaseIABProductDetailsDecreasingPriceComparator;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.R;
import com.tradehero.th.billing.BillingAlertDialogUtil;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.fragments.billing.googleplay.THIABStoreProductDetailView;
import com.tradehero.th.fragments.billing.googleplay.THSKUDetailAdapter;
import com.tradehero.th.utils.ActivityUtil;
import com.tradehero.th.utils.VersionUtils;
import com.tradehero.th.utils.metrics.Analytics;
import java.util.Comparator;
import java.util.HashMap;
import javax.inject.Inject;
import timber.log.Timber;

public class THIABAlertDialogUtil extends BillingAlertDialogUtil<
        IABSKU,
        THIABProductDetail,
        THIABLogicHolder,
        THIABStoreProductDetailView,
        THSKUDetailAdapter>
{
    protected THIABPurchaseCache thiabPurchaseCache;

    @Inject public THIABAlertDialogUtil(Analytics analytics, ActivityUtil activityUtil, THIABPurchaseCache thiabPurchaseCache)
    {
        super(analytics, activityUtil);
        this.thiabPurchaseCache = thiabPurchaseCache;
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
        return popSKUAlreadyOwned(context, null, null);
    }

    public <SKUDetailsType extends BaseIABProductDetail>
    AlertDialog popSKUAlreadyOwned(final Context context, SKUDetailsType skuDetails, DialogInterface.OnClickListener restoreClickListener)
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
                },
                restoreClickListener);
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

    public AlertDialog popResultError(final Context context)
    {
        return popWithNegativeButton(context, R.string.store_billing_result_error_window_title,
                R.string.store_billing_result_error_window_description,
                R.string.store_billing_result_error_cancel);
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

    public AlertDialog popFailedToLoadRequiredInfo(final Context context)
    {
        return popWithNegativeButton(context, R.string.store_billing_load_info_error_window_title,
                R.string.store_billing_load_info_error_window_description,
                R.string.store_billing_load_info_error_cancel);
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

    //<editor-fold desc="SKU related">
    @Override protected THSKUDetailAdapter createProductDetailAdapter(Activity activity,
            LayoutInflater layoutInflater, ProductIdentifierDomain skuDomain)
    {
        return new THSKUDetailAdapter(activity, layoutInflater, skuDomain);
    }

    @Override protected Comparator<THIABProductDetail> createProductDetailComparator()
    {
        return new BaseIABProductDetailsDecreasingPriceComparator<>();
    }

    @Override public HashMap<ProductIdentifier, Boolean> getEnabledItems()
    {
        HashMap<ProductIdentifier, Boolean> enabledItems = new HashMap<>();

        for (THIABPurchase key : thiabPurchaseCache.getValues())
        {
            Timber.d("Disabling %s", key);
            enabledItems.put(key.getProductIdentifier(), false);
        }

        if (enabledItems.size() == 0)
        {
            enabledItems = null;
        }
        return enabledItems;
    }
    //</editor-fold>
}
