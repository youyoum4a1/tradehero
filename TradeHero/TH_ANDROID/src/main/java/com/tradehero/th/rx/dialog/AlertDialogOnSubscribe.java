package com.tradehero.th.rx.dialog;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import rx.Observable;
import rx.Subscriber;
import rx.android.internal.Assertions;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

class AlertDialogOnSubscribe implements Observable.OnSubscribe<OnDialogClickEvent>
{
    @NonNull final AlertDialogRx.Builder builder;

    //<editor-fold desc="Constructors">
    protected AlertDialogOnSubscribe(@NonNull AlertDialogRx.Builder builder)
    {
        this.builder = builder;
    }
    //</editor-fold>

    @Override public void call(final Subscriber<? super OnDialogClickEvent> subscriber)
    {
        Assertions.assertUiThread();
        AlertDialog.Builder dialogBuilder = builder.dialogBuilder;

        DialogInterface.OnClickListener passingOnListener = new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.dismiss();
                subscriber.onNext(new OnDialogClickEvent(dialogInterface, i));
            }
        };
        if (builder.positiveButtonRes != null)
        {
            dialogBuilder.setPositiveButton(
                    builder.positiveButtonRes,
                    passingOnListener);
        }
        else
        {
            dialogBuilder.setPositiveButton(
                    builder.positiveButton,
                    passingOnListener);
        }

        if (builder.negativeButtonRes != null)
        {
            dialogBuilder.setNegativeButton(
                    builder.negativeButtonRes,
                    passingOnListener);
        }
        else
        {
            dialogBuilder.setNegativeButton(
                    builder.negativeButton,
                    passingOnListener);
        }

        if (builder.neutralButtonRes != null)
        {
            dialogBuilder.setNeutralButton(
                    builder.neutralButtonRes,
                    passingOnListener);
        }
        else
        {
            dialogBuilder.setNeutralButton(
                    builder.neutralButton,
                    passingOnListener);
        }

        if (builder.singleChoiceAdapter != null)
        {
            dialogBuilder.setSingleChoiceItems(
                    builder.singleChoiceAdapter,
                    builder.singleChoiceCheckedItem,
                    passingOnListener);
        }

        final AlertDialog dialog = dialogBuilder.create();
        if (builder.alertDialogObserver != null)
        {
            builder.alertDialogObserver.onNext(dialog);
            builder.alertDialogObserver.onCompleted();
        }
        subscriber.add(Subscriptions.create(new Action0()
        {
            @Override public void call()
            {
                dialog.dismiss();
            }
        }));
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override public void onDismiss(DialogInterface dialogInterface)
            {
                subscriber.onCompleted();
            }
        });
        try
        {
            dialog.show();
            dialog.setCanceledOnTouchOutside(builder.canceledOnTouchOutside);
        } catch (Throwable e)
        {
            subscriber.onError(e);
        }
    }
}
