package com.tradehero.th.billing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.tradehero.th.R;
import com.tradehero.th.utils.AlertDialogUtil;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class BillingAlertDialogUtil extends AlertDialogUtil
{
    public BillingAlertDialogUtil()
    {
        super();
    }

    public AlertDialog popBillingUnavailable(final Context context, String storeName)
    {
        return popWithOkCancelButton(context,
                context.getString(R.string.store_billing_unavailable_window_title),
                context.getString(R.string.store_billing_unavailable_window_description, storeName),
                R.string.store_billing_unavailable_act,
                R.string.store_billing_unavailable_cancel,
                new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        goToCreateAccount(context);
                    }
                });
    }

    abstract public void goToCreateAccount(final Context context);
}
