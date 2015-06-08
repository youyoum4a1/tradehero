package com.tradehero.th.rx.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import rx.Observable;
import rx.Subscriber;
import rx.android.internal.Assertions;

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

        AlertDialog dialog = dialogBuilder.create();
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
