package com.tradehero.th.fragments.trade;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.utils.AlertDialogUtil;

/** Created with IntelliJ IDEA. User: xavier Date: 11/20/13 Time: 4:07 PM To change this template use File | Settings | File Templates. */
public class AlertDialogUtilBuySell extends AlertDialogUtil
{
    public static final String TAG = AlertDialogUtilBuySell.class.getSimpleName();

    public static void informBuySellOrderWasNull(Context context)
    {
        popWithNegativeButton(context,
                R.string.alert_dialog_buy_sell_order_null_title,
                R.string.alert_dialog_buy_sell_order_null_message,
                R.string.alert_dialog_buy_sell_order_null_cancel);
    }

    public static void informBuySellOrderFailedRetrofit(Context context)
    {
        popWithNegativeButton(context,
                R.string.alert_dialog_buy_sell_retrofit_failed_title,
                R.string.alert_dialog_buy_sell_retrofit_failed_message,
                R.string.alert_dialog_buy_sell_retrofit_failed_cancel);
    }

    public static void informBuySellOrderReturnedNull(Context context)
    {
        popWithNegativeButton(context,
                R.string.alert_dialog_buy_sell_returned_null_title,
                R.string.alert_dialog_buy_sell_returned_null_message,
                R.string.alert_dialog_buy_sell_returned_null_cancel);
    }

    public static void informErrorWithMessage(Context context, String message)
    {
        popWithNegativeButton(context,
                context.getString(R.string.alert_dialog_buy_sell_returned_null_title),
                message,
                context.getString(R.string.alert_dialog_buy_sell_returned_null_cancel));
    }
}
