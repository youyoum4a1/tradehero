package com.tradehero.th.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.th.R;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.rx.dialog.AlertDialogRx;
import java.util.concurrent.CancellationException;
import javax.inject.Inject;
import rx.Observable;

public class AlertDialogRxUtil
{
    @NonNull protected final VersionUtils versionUtils;

    //<editor-fold desc="Constructors">
    @Inject public AlertDialogRxUtil(@NonNull VersionUtils versionUtils)
    {
        super();
        this.versionUtils = versionUtils;
    }
    //</editor-fold>

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

    @NonNull public Observable<Pair<DialogInterface, Integer>> popNetworkUnavailable(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.not_connected)
                .setMessage(R.string.not_connected_desc)
                .setPositiveButton(R.string.ok)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popErrorMessage(
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
}
