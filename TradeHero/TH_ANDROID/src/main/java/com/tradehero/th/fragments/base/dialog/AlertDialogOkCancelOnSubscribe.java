package com.tradehero.th.fragments.base.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import com.tradehero.th.utils.AlertDialogUtil;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Subscriber;

public class AlertDialogOkCancelOnSubscribe implements Observable.OnSubscribe<DialogResult>
{
    @NotNull final AlertDialogUtil alertDialogUtil;
    @NotNull final Activity activity;
    @NotNull final String title;
    @NotNull final String description;
    final int okResId;
    final int cancelResId;

    //<editor-fold desc="Constructors">
    public AlertDialogOkCancelOnSubscribe(
            @NotNull AlertDialogUtil alertDialogUtil,
            @NotNull Activity activity,
            @NotNull String title,
            @NotNull String description,
            int okResId,
            int cancelResId)
    {
        this.alertDialogUtil = alertDialogUtil;
        this.activity = activity;
        this.title = title;
        this.description = description;
        this.okResId = okResId;
        this.cancelResId = cancelResId;
    }
    //</editor-fold>

    @Override public void call(final Subscriber<? super DialogResult> subscriber)
    {
        alertDialogUtil.popWithOkCancelButton(
                activity,
                title,
                description,
                okResId,
                cancelResId,
                new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        subscriber.onNext(DialogResult.OK);
                    }
                },
                new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        subscriber.onNext(DialogResult.CANCEL);
                    }
                },
                new DialogInterface.OnDismissListener()
                {
                    @Override public void onDismiss(DialogInterface dialogInterface)
                    {
                        subscriber.onCompleted();
                    }
                });
    }
}
