package com.tradehero.th.fragments.trade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.utils.AlertDialogRxUtil;
import rx.Observable;

public class AlertDialogBuySellRxUtil extends AlertDialogRxUtil
{
    @SuppressLint("StringFormatMatches")
    @NonNull public static Observable<Pair<DialogInterface, Integer>> popMarketClosed(
            @NonNull final Context activityContext,
            @Nullable SecurityId securityId)
    {
        String message;
        if (securityId == null)
        {
            message = activityContext.getString(R.string.alert_dialog_market_close_message_basic);
        }
        else
        {
            message = activityContext.getString(
                    R.string.alert_dialog_market_close_message,
                    securityId.getExchange(),
                    securityId.getSecuritySymbol());
        }

        return buildDefault(activityContext)
                .setIcon(R.drawable.market_sleep_grey)
                .setTitle(R.string.alert_dialog_market_close_title)
                .setMessage(message)
                .setPositiveButton(R.string.alert_dialog_market_close_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @NonNull public static Observable<Pair<DialogInterface, Integer>> informBuySellOrderWasNull(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.alert_dialog_buy_sell_order_null_title)
                .setMessage(R.string.alert_dialog_buy_sell_order_null_message)
                .setPositiveButton(R.string.alert_dialog_buy_sell_order_null_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @NonNull public static Observable<Pair<DialogInterface, Integer>> informBuySellOrderFailedRetrofit(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.alert_dialog_buy_sell_retrofit_failed_title)
                .setMessage(R.string.alert_dialog_buy_sell_retrofit_failed_message)
                .setPositiveButton(R.string.alert_dialog_buy_sell_retrofit_failed_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @NonNull public static Observable<Pair<DialogInterface, Integer>> informBuySellOrderReturnedNull(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.alert_dialog_buy_sell_returned_null_title)
                .setMessage(R.string.alert_dialog_buy_sell_returned_null_message)
                .setPositiveButton(R.string.alert_dialog_buy_sell_returned_null_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @NonNull public static Observable<Pair<DialogInterface, Integer>> informErrorWithMessage(
            @NonNull final Context activityContext,
            @NonNull String message)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.alert_dialog_buy_sell_returned_null_title)
                .setMessage(message)
                .setPositiveButton(R.string.alert_dialog_buy_sell_returned_null_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }
}
