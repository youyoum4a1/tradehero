package com.ayondo.academy.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.misc.exception.THException;
import com.ayondo.academy.rx.dialog.AlertDialogRx;
import com.ayondo.academy.rx.dialog.OnDialogClickEvent;
import java.util.concurrent.CancellationException;
import rx.Observable;

public class AlertDialogRxUtil
{
    @NonNull public static AlertDialogRx.Builder buildDefault(@NonNull Context activityContext)
    {
        return build(activityContext)
                .setIcon(R.drawable.th_app_logo)
                .setCancelable(true);
    }

    @NonNull public static AlertDialogRx.Builder build(@NonNull Context activityContext)
    {
        return AlertDialogRx.build(activityContext);
    }

    @NonNull public static Observable<OnDialogClickEvent> popNetworkUnavailable(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.not_connected)
                .setMessage(R.string.not_connected_desc)
                .setPositiveButton(R.string.ok)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @NonNull public static Observable<OnDialogClickEvent> popErrorMessage(
            @NonNull final Context activityContext,
            @NonNull final Throwable error)
    {
        if (error instanceof CancellationException)
        {
            return Observable.empty();
        }
        THException reprocessed = new THException(error);
        String errorMessage = reprocessed.getMessage();
        if (errorMessage == null)
        {
            errorMessage = error.getClass().getSimpleName();
        }
        return buildDefault(activityContext)
                .setTitle(R.string.error)
                .setMessage(errorMessage)
                .setPositiveButton(R.string.ok)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @NonNull public static strictfp Observable<OnDialogClickEvent> popUpgradeRequired(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.upgrade_needed)
                .setMessage(R.string.please_update)
                .setPositiveButton(R.string.update_now)
                .setNegativeButton(R.string.later)
                .setCanceledOnTouchOutside(true)
                .build();
    }
}
