package com.tradehero.th.billing.googleplay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.localytics.android.LocalyticsSession;
import com.tradehero.th.R;
import com.tradehero.th.utils.ActivityUtil;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 5:28 PM To change this template use File | Settings | File Templates. */
public class THIABPurchaseRestorerAlertUtil extends THIABAlertDialogUtil
{
    @Inject public THIABPurchaseRestorerAlertUtil(LocalyticsSession localyticsSession, ActivityUtil activityUtil, THIABPurchaseCache thiabPurchaseCache)
    {
        super(localyticsSession, activityUtil, thiabPurchaseCache);
    }

    public AlertDialog handlePurchaseRestoreFinished(final Context context, List<THIABPurchase> consumed, List<THIABPurchase> reportFailed, List<THIABPurchase> consumeFailed, final DialogInterface.OnClickListener clickListener)
    {
        return handlePurchaseRestoreFinished(context, consumed, reportFailed, consumeFailed, clickListener, false);
    }

    public AlertDialog handlePurchaseRestoreFinished(final Context context, List<THIABPurchase> consumed, List<THIABPurchase> reportFailed, List<THIABPurchase> consumeFailed, final DialogInterface.OnClickListener clickListener, boolean verbose)
    {
        int countOk = (consumed == null ? 0 : consumed.size());
        int countReportFailed = (reportFailed == null ? 0 : reportFailed.size());
        int countConsumeFailed = (consumeFailed == null ? 0 : consumeFailed.size());
        int countFailed = countReportFailed + countConsumeFailed;

        AlertDialog alertDialog = null;
        if ((reportFailed == null || reportFailed.size() == 0) &&
                (consumeFailed == null || consumeFailed.size() == 0) &&
                consumed != null && consumed.size() > 0)
        {
            alertDialog = popPurchasesRestored(context, consumed.size());
        }
        else if ((reportFailed != null && reportFailed.size() > 0) ||
                (consumeFailed != null && consumeFailed.size() > 0))
        {
            alertDialog = popSendEmailSupportRestorePartiallyFailed(context, clickListener, countOk, countFailed);
        }
        else if (verbose && (reportFailed == null || reportFailed.size() == 0) &&
                (consumeFailed == null || consumeFailed.size() == 0) &&
                (consumed == null || consumed.size() == 0))
        {
            alertDialog = popNoPurchaseToRestore(context);
        }

        if (alertDialog != null)
        {
            alertDialog.setCanceledOnTouchOutside(true);
        }
        Timber.d("Restored purchases: %d, failed report: %d, failed consume: %d", countOk, countReportFailed, countConsumeFailed);
        return alertDialog;
    }

    public AlertDialog popNoPurchaseToRestore(final Context context)
    {
        return popWithNegativeButton(context,
                context.getString(R.string.google_play_purchase_restored_none_title),
                String.format(context.getString(R.string.google_play_purchase_restored_none_message)),
                context.getString(R.string.google_play_purchase_restored_none_cancel));
    }

    public AlertDialog popPurchasesRestored(final Context context, final int countOk)
    {
        return popWithNegativeButton(context,
                context.getString(R.string.google_play_purchase_restored_title),
                String.format(context.getString(R.string.google_play_purchase_restored_message), countOk),
                context.getString(R.string.google_play_purchase_restored_cancel));
    }

    public AlertDialog popSendEmailSupportRestorePartiallyFailed(final Context context, final DialogInterface.OnClickListener clickListener, final int countOk, final int countFailed)
    {
        return popWithOkCancelButton(context,
                context.getString(R.string.google_play_send_support_email_restore_fail_partial_title),
                String.format(context.getString(R.string.google_play_send_support_email_restore_fail_partial_message), countOk, countFailed),
                R.string.google_play_send_support_email_restore_fail_partial_ok,
                R.string.google_play_send_support_email_restore_fail_partial_cancel,
                clickListener);
    }

    public AlertDialog popSendEmailSupportRestoreFailed(final Context context, final Exception exception)
    {
        return popSendEmailSupportRestoreFailed(context, createFailedRestoreClickListener(context, exception));
    }

    public AlertDialog popSendEmailSupportRestoreFailed(final Context context, final DialogInterface.OnClickListener clickListener)
    {
        return popWithOkCancelButton(context,
                R.string.google_play_send_support_email_restore_fail_title,
                R.string.google_play_send_support_email_restore_fail_message,
                R.string.google_play_send_support_email_restore_fail_ok,
                R.string.google_play_send_support_email_restore_fail_cancel,
                clickListener);
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
                GooglePlayUtils.getSupportPurchaseRestoreEmailIntent(context, exception),
                context.getString(R.string.google_play_send_support_email_chooser_title)));
    }
}
