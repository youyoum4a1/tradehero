package com.tradehero.th.fragments.trade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.rx.dialog.AlertDialogOnSubscribe;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.VersionUtils;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class AlertDialogBuySellRxUtil extends AlertDialogRxUtil
{
    //<editor-fold desc="Constructors">
    @Inject public AlertDialogBuySellRxUtil(@NonNull VersionUtils versionUtils)
    {
        super(versionUtils);
    }
    //</editor-fold>

    @SuppressLint("StringFormatMatches")
    @NonNull public Observable<Pair<DialogInterface, Integer>> popMarketClosed(
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

        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setIcon(R.drawable.market_sleep_grey)
                        .setTitle(R.string.alert_dialog_market_close_title)
                        .setMessage(message))
                .setPositiveButton(R.string.alert_dialog_market_close_cancel)
                .setCanceledOnTouchOutside(true)
                .build())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> informBuySellOrderWasNull(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.alert_dialog_buy_sell_order_null_title)
                        .setMessage(R.string.alert_dialog_buy_sell_order_null_message))
                .setPositiveButton(R.string.alert_dialog_buy_sell_order_null_cancel)
                .setCanceledOnTouchOutside(true)
                .build())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> informBuySellOrderFailedRetrofit(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.alert_dialog_buy_sell_retrofit_failed_title)
                        .setMessage(R.string.alert_dialog_buy_sell_retrofit_failed_message))
                .setPositiveButton(R.string.alert_dialog_buy_sell_retrofit_failed_cancel)
                .setCanceledOnTouchOutside(true)
                .build())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> informBuySellOrderReturnedNull(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.alert_dialog_buy_sell_returned_null_title)
                        .setMessage(R.string.alert_dialog_buy_sell_returned_null_message))
                .setPositiveButton(R.string.alert_dialog_buy_sell_returned_null_cancel)
                .setCanceledOnTouchOutside(true)
                .build())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> informErrorWithMessage(
            @NonNull final Context activityContext,
            @NonNull String message)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.alert_dialog_buy_sell_returned_null_title)
                        .setMessage(message))
                .setPositiveButton(R.string.alert_dialog_buy_sell_returned_null_cancel)
                .setCanceledOnTouchOutside(true)
                .build())
                .subscribeOn(AndroidSchedulers.mainThread());
    }
}
