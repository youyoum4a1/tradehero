package com.tradehero.th.billing.googleplay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.th.R;
import com.tradehero.th.utils.ActivityUtil;
import com.tradehero.th.utils.metrics.Analytics;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class THIABPurchaseRestorerAlertUtil extends THIABAlertDialogUtil
{
    @Inject public THIABPurchaseRestorerAlertUtil(Analytics analytics, ActivityUtil activityUtil, THIABPurchaseCache thiabPurchaseCache)
    {
        super(analytics, activityUtil, thiabPurchaseCache);
    }

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

    public AlertDialog popNoPurchaseToRestore(final Context context)
    {
        return popWithNegativeButton(context,
                context.getString(R.string.google_play_purchase_restored_none_title),
                context.getString(R.string.google_play_purchase_restored_none_message),
                context.getString(R.string.google_play_purchase_restored_none_cancel));
    }

    public AlertDialog popPurchasesRestored(final Context context, final int countOk)
    {
        return popWithNegativeButton(context,
                context.getString(R.string.google_play_purchase_restored_title),
                context.getString(R.string.google_play_purchase_restored_message, countOk),
                context.getString(R.string.google_play_purchase_restored_cancel));
    }

    public AlertDialog popSendEmailSupportRestorePartiallyFailed(final Context context, final DialogInterface.OnClickListener clickListener, final int countOk, final int countFailed)
    {
        return popWithOkCancelButton(context,
                context.getString(R.string.google_play_send_support_email_restore_fail_partial_title),
                context.getString(R.string.google_play_send_support_email_restore_fail_partial_message, countOk, countFailed),
                R.string.google_play_send_support_email_restore_fail_partial_ok,
                R.string.google_play_send_support_email_restore_fail_partial_cancel,
                clickListener);
    }

    public AlertDialog popSendEmailSupportRestoreFailed(final Context context, int count, final DialogInterface.OnClickListener clickListener)
    {
        return popWithOkCancelButton(context,
                context.getString(R.string.google_play_send_support_email_restore_fail_title),
                context.getString(R.string.google_play_send_support_email_restore_fail_message, count),
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
