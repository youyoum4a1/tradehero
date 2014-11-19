package com.tradehero.th.rx.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Pair;
import android.widget.ListAdapter;
import rx.Observable;
import rx.Subscriber;

public class AlertDialogOnSubscribe implements Observable.OnSubscribe<Pair<DialogInterface, Integer>>
{
    @NonNull final Builder builder;

    //<editor-fold desc="Constructors">
    public AlertDialogOnSubscribe(@NonNull Builder builder)
    {
        this.builder = builder;
    }
    //</editor-fold>

    @Override public void call(final Subscriber<? super Pair<DialogInterface, Integer>> subscriber)
    {
        DialogInterface.OnClickListener passingOnListener = new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialogInterface, int i)
            {
                subscriber.onNext(Pair.create(dialogInterface, i));
            }
        };
        if (builder.positiveButtonRes != null)
        {
            builder.alertDialogBuilder.setPositiveButton(
                    builder.positiveButtonRes,
                    passingOnListener);
        }
        else
        {
            builder.alertDialogBuilder.setPositiveButton(
                    builder.positiveButton,
                    passingOnListener);
        }

        if (builder.negativeButtonRes != null)
        {
            builder.alertDialogBuilder.setNegativeButton(
                    builder.negativeButtonRes,
                    passingOnListener);
        }
        else
        {
            builder.alertDialogBuilder.setNegativeButton(
                    builder.negativeButton,
                    passingOnListener);
        }

        if (builder.neutralButtonRes != null)
        {
            builder.alertDialogBuilder.setNeutralButton(
                    builder.neutralButtonRes,
                    passingOnListener);
        }
        else
        {
            builder.alertDialogBuilder.setNeutralButton(
                    builder.neutralButton,
                    passingOnListener);
        }

        if (builder.singleChoicedapter != null)
        {
            builder.alertDialogBuilder.setSingleChoiceItems(
                    builder.singleChoicedapter,
                    builder.singleChoiceCheckedItem,
                    passingOnListener);
        }

        AlertDialog dialog = builder.alertDialogBuilder.create();
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

    public static Builder builder(@NonNull AlertDialog.Builder alertDialogBuilder)
    {
        return new Builder(alertDialogBuilder);
    }

    public static class Builder
    {
        @Nullable private CharSequence positiveButton;
        @Nullable @StringRes private Integer positiveButtonRes;
        @Nullable private CharSequence negativeButton;
        @Nullable @StringRes private Integer negativeButtonRes;
        @Nullable private CharSequence neutralButton;
        @Nullable @StringRes private Integer neutralButtonRes;
        private boolean canceledOnTouchOutside;
        @Nullable private ListAdapter singleChoicedapter;
        private int singleChoiceCheckedItem;

        @NonNull private final AlertDialog.Builder alertDialogBuilder;

        public Builder(@NonNull AlertDialog.Builder alertDialogBuilder)
        {
            this.alertDialogBuilder = alertDialogBuilder;
        }

        public Builder setPositiveButton(@Nullable CharSequence positiveButton)
        {
            this.positiveButton = positiveButton;
            return this;
        }

        public Builder setPositiveButton(int positiveButton)
        {
            this.positiveButtonRes = positiveButton;
            return this;
        }

        public Builder setNegativeButton(@Nullable CharSequence negativeButton)
        {
            this.negativeButton = negativeButton;
            return this;
        }

        public Builder setNegativeButton(int negativeButton)
        {
            this.negativeButtonRes = negativeButton;
            return this;
        }

        public Builder setNeutralButton(@Nullable CharSequence neutralButton)
        {
            this.neutralButton = neutralButton;
            return this;
        }

        public Builder setNeutralButton(int neutralButton)
        {
            this.neutralButtonRes = neutralButton;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside)
        {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public Builder setSingleChoiceItems(@NonNull ListAdapter adapter, int checkedItem)
        {
            this.singleChoicedapter = adapter;
            this.singleChoiceCheckedItem = checkedItem;
            return this;
        }

        public AlertDialogOnSubscribe build()
        {
            return new AlertDialogOnSubscribe(this);
        }
    }
}
