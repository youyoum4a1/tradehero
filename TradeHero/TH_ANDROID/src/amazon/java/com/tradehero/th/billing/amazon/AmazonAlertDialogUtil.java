package com.tradehero.th.billing.amazon;

import android.app.AlertDialog;
import android.content.Context;
import com.tradehero.common.billing.amazon.exception.AmazonPurchaseUnsupportedException;

public interface AmazonAlertDialogUtil
{
    AlertDialog popPurchaseUnsupportedError(final Context context, final AmazonPurchaseUnsupportedException exception);
    AlertDialog popSandboxMode(final Context context);
}
