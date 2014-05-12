package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.googleplay.BaseIABProductDetail;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.R;
import com.tradehero.th.billing.BillingAlertDialogUtil;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THProductDetailDecreasingPriceComparator;
import com.tradehero.th.fragments.billing.googleplay.THIABSKUDetailAdapter;
import com.tradehero.th.fragments.billing.googleplay.THIABStoreProductDetailView;
import com.tradehero.th.utils.ActivityUtil;
import com.tradehero.th.utils.VersionUtils;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class THIABAlertDialogUtil extends BillingAlertDialogUtil<
        IABSKU,
        THIABProductDetail,
        THIABLogicHolder,
        THIABStoreProductDetailView,
        THIABSKUDetailAdapter>
{
    protected THIABPurchaseCache thiabPurchaseCache;
    protected GooglePlayUtils googlePlayUtils;

    @Inject public THIABAlertDialogUtil(LocalyticsSession localyticsSession, ActivityUtil activityUtil, THIABPurchaseCache thiabPurchaseCache, GooglePlayUtils googlePlayUtils)
    {
        super(localyticsSession, activityUtil);
        this.thiabPurchaseCache = thiabPurchaseCache;
        this.googlePlayUtils = googlePlayUtils;
    }

    public AlertDialog popVerificationFailed(final Context context)
    {
        return popWithNegativeButton(context, R.string.google_play_billing_verification_failed_window_title,
                R.string.google_play_billing_verification_failed_window_description,
                R.string.google_play_billing_verification_failed_cancel);
    }

    public AlertDialog popBadResponse(final Context context)
    {
        return popWithNegativeButton(context,
                R.string.google_play_billing_bad_response_window_title,
                R.string.google_play_billing_bad_response_window_description,
                R.string.google_play_billing_bad_response_cancel);
    }

    public AlertDialog popResultError(final Context context)
    {
        return popWithNegativeButton(context,
                R.string.google_play_billing_result_error_window_title,
                R.string.google_play_billing_result_error_window_description,
                R.string.google_play_billing_result_error_cancel);
    }

    public AlertDialog popSendIntent(final Context context)
    {
        return popWithNegativeButton(context, R.string.google_play_billing_send_intent_error_window_title,
                R.string.google_play_billing_send_intent_error_window_description,
                R.string.google_play_billing_send_intent_error_cancel);
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
        activityUtil.sendSupportEmail(context, googlePlayUtils.getSupportPurchaseConsumeEmailIntent(context, exception));
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
    @Override protected THIABSKUDetailAdapter createProductDetailAdapter(Activity activity,
            LayoutInflater layoutInflater, ProductIdentifierDomain skuDomain)
    {
        return new THIABSKUDetailAdapter(activity, layoutInflater, skuDomain);
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

    //<editor-fold desc="Restore Related">
    @Deprecated // TODO user list of exceptions
    public AlertDialog handlePurchaseRestoreFinished(final Context context, List<? extends ProductPurchase> restored, List<? extends ProductPurchase> restoreFailed, final DialogInterface.OnClickListener clickListener)
    {
        return handlePurchaseRestoreFinished(context, restored, restoreFailed, clickListener, false);
    }

    @Deprecated // TODO user list of exceptions
    public AlertDialog handlePurchaseRestoreFinished(final Context context, List<? extends ProductPurchase> restored, List<? extends ProductPurchase> restoreFailed, final DialogInterface.OnClickListener clickListener, boolean verbose)
    {
        int countOk = (restored == null ? 0 : restored.size());
        int countRestoreFailed = (restoreFailed == null ? 0 : restoreFailed.size());

        AlertDialog alertDialog = null;
        if (countRestoreFailed == 0 && countOk > 0)
        {
            alertDialog = popPurchasesRestored(context, countOk);
        }
        else if (countRestoreFailed > 0 && countOk == 0)
        {
            // TODO
            alertDialog = popSendEmailSupportRestoreFailed(context, countRestoreFailed, clickListener);
        }
        else if (countRestoreFailed > 0)
        {
            alertDialog = popSendEmailSupportRestorePartiallyFailed(context, clickListener, countOk, countRestoreFailed);
        }
        else if (verbose && countRestoreFailed == 0 && countOk == 0)
        {
            alertDialog = popNoPurchaseToRestore(context);
        }

        if (alertDialog != null)
        {
            alertDialog.setCanceledOnTouchOutside(true);
        }
        Timber.d("Restored purchases: %d, failed restore: %d", countOk, countRestoreFailed);
        return alertDialog;
    }

    public DialogInterface.OnClickListener createFailedRestoreClickListener(final Context context, final Exception exception)
    {
        return new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                sendSupportEmailRestoreFailed(context, exception);
            }
        };
    }

    public void sendSupportEmailRestoreFailed(final Context context, Exception exception)
    {
        context.startActivity(Intent.createChooser(
                googlePlayUtils.getSupportPurchaseRestoreEmailIntent(context, exception),
                context.getString(R.string.iap_send_support_email_chooser_title)));
    }
    //</editor-fold>

}
