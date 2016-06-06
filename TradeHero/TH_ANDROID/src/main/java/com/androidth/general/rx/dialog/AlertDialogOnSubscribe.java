package com.androidth.general.rx.dialog;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.ListAdapter;
import rx.Observable;
import rx.Observer;
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
        Integer positiveButtonRes = builder.positiveButtonRes;
        if (positiveButtonRes != null)
        {
            dialogBuilder.setPositiveButton(
                    positiveButtonRes,
                    passingOnListener);
        }
        else
        {
            dialogBuilder.setPositiveButton(
                    builder.positiveButton,
                    passingOnListener);
        }

        Integer negativeButtonRes = builder.negativeButtonRes;
        if (negativeButtonRes != null)
        {
            dialogBuilder.setNegativeButton(
                    negativeButtonRes,
                    passingOnListener);
        }
        else
        {
            dialogBuilder.setNegativeButton(
                    builder.negativeButton,
                    passingOnListener);
        }

        Integer neutralButtonRes = builder.neutralButtonRes;
        if (neutralButtonRes != null)
        {
            dialogBuilder.setNeutralButton(
                    neutralButtonRes,
                    passingOnListener);
        }
        else
        {
            dialogBuilder.setNeutralButton(
                    builder.neutralButton,
                    passingOnListener);
        }

        ListAdapter singleChoiceAdapter = builder.singleChoiceAdapter;
        if (singleChoiceAdapter != null)
        {
            dialogBuilder.setSingleChoiceItems(
                    singleChoiceAdapter,
                    builder.singleChoiceCheckedItem,
                    passingOnListener);
        }

        final AlertDialog dialog = dialogBuilder.create();
        Observer<AlertDialog> alertDialogObserver = builder.alertDialogObserver;
        if (alertDialogObserver != null)
        {
            alertDialogObserver.onNext(dialog);
            alertDialogObserver.onCompleted();
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
