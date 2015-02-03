package com.tradehero.th.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.th.R;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.rx.dialog.AlertDialogOnSubscribe;
import java.util.concurrent.CancellationException;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

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

    @NonNull public AlertDialog.Builder createDefaultDialogBuilder(@NonNull Context activityContext)
    {
        return new AlertDialog.Builder(activityContext)
                .setIcon(R.drawable.th_app_logo)
                .setCancelable(true);
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popNetworkUnavailable(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.not_connected)
                        .setMessage(R.string.not_connected_desc))
                .setPositiveButton(R.string.ok)
                .setCanceledOnTouchOutside(true)
                .build())
                .subscribeOn(AndroidSchedulers.mainThread());
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
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.error)
                        .setMessage(errorMessage))
                .setPositiveButton(R.string.ok)
                .setCanceledOnTouchOutside(true)
                .build())
                .subscribeOn(AndroidSchedulers.mainThread());
    }
}
