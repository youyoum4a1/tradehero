package com.tradehero.th.fragments.trade;

import android.content.Context;
import com.tradehero.thm.R;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;

public class AlertDialogUtilBuySell extends AlertDialogUtil
{
    @Inject public AlertDialogUtilBuySell()
    {
        super();
    }

    public void informBuySellOrderWasNull(Context context)
    {
        popWithNegativeButton(context,
                R.string.alert_dialog_buy_sell_order_null_title,
                R.string.alert_dialog_buy_sell_order_null_message,
                R.string.alert_dialog_buy_sell_order_null_cancel);
    }

    public void informBuySellOrderFailedRetrofit(Context context)
    {
        popWithNegativeButton(context,
                R.string.alert_dialog_buy_sell_retrofit_failed_title,
                R.string.alert_dialog_buy_sell_retrofit_failed_message,
                R.string.alert_dialog_buy_sell_retrofit_failed_cancel);
    }

    public void informBuySellOrderReturnedNull(Context context)
    {
        popWithNegativeButton(context,
                R.string.alert_dialog_buy_sell_returned_null_title,
                R.string.alert_dialog_buy_sell_returned_null_message,
                R.string.alert_dialog_buy_sell_returned_null_cancel);
    }

    public void informErrorWithMessage(Context context, String message)
    {
        popWithNegativeButton(context,
                context.getString(R.string.alert_dialog_buy_sell_returned_null_title),
                message,
                context.getString(R.string.alert_dialog_buy_sell_returned_null_cancel));
    }
}
