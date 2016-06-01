package com.ayondo.academy.billing.amazon;

import android.content.Context;
import android.support.annotation.NonNull;
import com.ayondo.academy.rx.dialog.OnDialogClickEvent;
import rx.Observable;

public interface AmazonAlertDialogRxUtil
{
    @NonNull public Observable<OnDialogClickEvent> popInventoryFailedAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable);

    @NonNull public Observable<OnDialogClickEvent> popInventoryFailed(
            @NonNull final Context activityContext);

    @NonNull public Observable<OnDialogClickEvent> popInventoryNotSupportedAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable);

    @NonNull public Observable<OnDialogClickEvent> popInventoryNotSupported(
            @NonNull final Context activityContext);

    @NonNull Observable<OnDialogClickEvent> popPurchaseUnsupportedAndHandle(
            @NonNull final Context context,
            @NonNull final Throwable throwable);

    @NonNull Observable<OnDialogClickEvent> popPurchaseUnsupported(
            @NonNull final Context context);

    @NonNull Observable<OnDialogClickEvent> popSandboxModeAndHandle(
            @NonNull final Context context);

    @NonNull Observable<OnDialogClickEvent> popSandboxMode(
            @NonNull final Context context);
}
