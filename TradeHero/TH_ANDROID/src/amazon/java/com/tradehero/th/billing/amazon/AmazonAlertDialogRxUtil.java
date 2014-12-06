package com.tradehero.th.billing.amazon;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Pair;
import rx.Observable;

public interface AmazonAlertDialogRxUtil
{
    @NonNull public Observable<Pair<DialogInterface, Integer>> popInventoryFailedAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable);

    @NonNull public Observable<Pair<DialogInterface, Integer>> popInventoryFailed(
            @NonNull final Context activityContext);

    @NonNull public Observable<Pair<DialogInterface, Integer>> popInventoryNotSupportedAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable);

    @NonNull public Observable<Pair<DialogInterface, Integer>> popInventoryNotSupported(
            @NonNull final Context activityContext);

    @NonNull Observable<Pair<DialogInterface, Integer>> popPurchaseUnsupportedAndHandle(
            @NonNull final Context context,
            @NonNull final Throwable throwable);

    @NonNull Observable<Pair<DialogInterface, Integer>> popPurchaseUnsupported(
            @NonNull final Context context);

    @NonNull Observable<Pair<DialogInterface, Integer>> popSandboxMode(
            @NonNull final Context context);
}
