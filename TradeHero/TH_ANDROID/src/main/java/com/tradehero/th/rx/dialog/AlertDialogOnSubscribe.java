package com.tradehero.th.rx.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;
import rx.Subscriber;

public class AlertDialogOnSubscribe implements Observable.OnSubscribe<Pair<DialogInterface, Integer>>
{
    @NotNull final Builder builder;

    //<editor-fold desc="Constructors">
    public AlertDialogOnSubscribe(@NotNull Builder builder)
    {
        this.builder = builder;
    }
    //</editor-fold>

    @Override public void call(final Subscriber<? super Pair<DialogInterface, Integer>> subscriber)
    {
        builder.alertDialogBuilder.setPositiveButton(
                builder.positiveButton,
                new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        subscriber.onNext(Pair.create(dialogInterface, i));
                    }
                });
        builder.alertDialogBuilder.setNegativeButton(
                builder.negativeButton,
                new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        subscriber.onNext(Pair.create(dialogInterface, i));
                    }
                });
        builder.alertDialogBuilder.setNeutralButton(
                builder.neutralButton,
                new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        subscriber.onNext(Pair.create(dialogInterface, i));
                    }
                });
        AlertDialog dialog = builder.alertDialogBuilder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override public void onDismiss(DialogInterface dialogInterface)
            {
                subscriber.onCompleted();
            }
        });
        dialog.show();
    }

    public static Builder builder(@NotNull AlertDialog.Builder alertDialogBuilder)
    {
        return new Builder(alertDialogBuilder);
    }

    public static class Builder
    {
        @Nullable private CharSequence positiveButton;
        @Nullable private CharSequence negativeButton;
        @Nullable private CharSequence neutralButton;
        @NotNull private final AlertDialog.Builder alertDialogBuilder;

        public Builder(@NotNull AlertDialog.Builder alertDialogBuilder)
        {
            this.alertDialogBuilder = alertDialogBuilder;
        }

        public Builder setPositiveButton(@Nullable CharSequence positiveButton)
        {
            this.positiveButton = positiveButton;
            return this;
        }

        public Builder setNegativeButton(@Nullable CharSequence negativeButton)
        {
            this.negativeButton = negativeButton;
            return this;
        }

        public Builder setNeutralButton(@Nullable CharSequence neutralButton)
        {
            this.neutralButton = neutralButton;
            return this;
        }

        public AlertDialogOnSubscribe build()
        {
            return new AlertDialogOnSubscribe(this);
        }
    }
}
