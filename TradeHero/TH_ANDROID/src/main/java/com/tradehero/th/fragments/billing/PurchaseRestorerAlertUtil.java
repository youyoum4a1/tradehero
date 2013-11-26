package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.billing.googleplay.GooglePlayUtils;
import com.tradehero.th.utils.AlertDialogUtil;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 5:28 PM To change this template use File | Settings | File Templates. */
public class PurchaseRestorerAlertUtil
{
    public static final String TAG = PurchaseRestorerAlertUtil.class.getSimpleName();

    public static void handlePurchaseRestoreFinished(final Context context, List<SKUPurchase> consumed, List<SKUPurchase> reportFailed, List<SKUPurchase> consumeFailed, final DialogInterface.OnClickListener clickListener)
    {
        int countOk = (consumed == null ? 0 : consumed.size());
        int countReportFailed = (reportFailed == null ? 0 : reportFailed.size());
        int countConsumeFailed = (consumeFailed == null ? 0 : consumeFailed.size());
        int countFailed = countReportFailed + countConsumeFailed;

        if ((reportFailed == null || reportFailed.size() == 0) &&
                (consumeFailed == null || consumeFailed.size() == 0) &&
                consumed != null && consumed.size() > 0)
        {
            popPurchasesRestored(context, consumed.size());
        }
        else if ((reportFailed != null && reportFailed.size() > 0) ||
                (consumeFailed != null && consumeFailed.size() > 0))
        {
            popSendEmailSupportRestorePartiallyFailed(context, clickListener, countOk, countFailed);
        }
        THLog.d(TAG, "Restored purchases: " + countOk + ", failed report: " + countReportFailed + ", failed consume: " + countConsumeFailed);
    }

    public static void popPurchasesRestored(final Context context, final int countOk)
    {
        AlertDialogUtil.popWithCancelButton(context,
                context.getString(R.string.google_play_purchase_restored_title),
                String.format(context.getString(R.string.google_play_purchase_restored_message), countOk),
                context.getString(R.string.google_play_purchase_restored_cancel));
    }

    public static void popSendEmailSupportRestorePartiallyFailed(final Context context, final DialogInterface.OnClickListener clickListener, final int countOk, final int countFailed)
    {
        AlertDialogUtil.popWithOkCancelButton(context,
                context.getString(R.string.google_play_send_support_email_restore_fail_partial_title),
                String.format(context.getString(R.string.google_play_send_support_email_restore_fail_partial_message), countOk, countFailed),
                R.string.google_play_send_support_email_restore_fail_partial_ok,
                R.string.google_play_send_support_email_restore_fail_partial_cancel,
                clickListener);
    }

    public static void popSendEmailSupportRestoreFailed(final Context context, final DialogInterface.OnClickListener clickListener)
    {
        THLog.d(TAG, "popSendEmailSupportRestoreFailed");
        AlertDialogUtil.popWithOkCancelButton(context,
                R.string.google_play_send_support_email_restore_fail_title,
                R.string.google_play_send_support_email_restore_fail_message,
                R.string.google_play_send_support_email_restore_fail_ok,
                R.string.google_play_send_support_email_restore_fail_cancel,
                clickListener);
    }

    public static void sendSupportEmailRestoreFailed(final Context context, Exception exception)
    {
        context.startActivity(Intent.createChooser(
                GooglePlayUtils.getSupportPurchaseRestoreEmailIntent(context, exception),
                context.getString(R.string.google_play_send_support_email_chooser_title)));
    }
}
