package com.androidth.general.rx.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListAdapter;
import com.androidth.general.R;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class AlertDialogRx
{
    @NonNull public static Builder build(@NonNull Context activityContext)
    {
        return new Builder(activityContext);
    }

    @NonNull public static Builder build(@NonNull AlertDialog.Builder dialogBuilder)
    {
        return new Builder(dialogBuilder);
    }

    public static class Builder
    {
        @Nullable CharSequence positiveButton;
        @Nullable @StringRes Integer positiveButtonRes;
        @Nullable CharSequence negativeButton;
        @Nullable @StringRes Integer negativeButtonRes;
        @Nullable CharSequence neutralButton;
        @Nullable @StringRes Integer neutralButtonRes;
        boolean canceledOnTouchOutside;
        @Nullable ListAdapter singleChoiceAdapter;
        int singleChoiceCheckedItem;
        @Nullable Observer<AlertDialog> alertDialogObserver;

        @NonNull final AlertDialog.Builder dialogBuilder;

        //<editor-fold desc="Constructors">
        public Builder(@NonNull Context activityContext)
        {
            this.dialogBuilder = new AlertDialog.Builder(activityContext)
                    .setIcon(R.drawable.th_app_logo);
        }

        public Builder(@NonNull AlertDialog.Builder dialogBuilder)
        {
            this.dialogBuilder = dialogBuilder;
        }
        //</editor-fold>

        @NonNull public Builder setCancelable(boolean cancelable)
        {
            dialogBuilder.setCancelable(cancelable);
            return this;
        }

        @NonNull public Builder setIcon(@DrawableRes int iconRes)
        {
            dialogBuilder.setIcon(iconRes);
            return this;
        }

        @NonNull public Builder setIcon(@Nullable Drawable icon)
        {
            dialogBuilder.setIcon(icon);
            return this;
        }

        @NonNull public Builder setTitle(@StringRes int titleRes)
        {
            dialogBuilder.setTitle(titleRes);
            return this;
        }

        @NonNull public Builder setTitle(@Nullable String title)
        {
            dialogBuilder.setTitle(title);
            return this;
        }

        @NonNull public Builder setMessage(@StringRes int messageRes)
        {
            dialogBuilder.setMessage(messageRes);
            return this;
        }

        @NonNull public Builder setMessage(@Nullable String message)
        {
            dialogBuilder.setMessage(message);
            return this;
        }

        @NonNull public Builder setView(@Nullable View view)
        {
            dialogBuilder.setView(view);
            return this;
        }

        @NonNull public Builder setPositiveButton(@Nullable CharSequence positiveButton)
        {
            this.positiveButton = positiveButton;
            return this;
        }

        @NonNull public Builder setPositiveButton(int positiveButton)
        {
            this.positiveButtonRes = positiveButton;
            return this;
        }

        @NonNull public Builder setNegativeButton(@Nullable CharSequence negativeButton)
        {
            this.negativeButton = negativeButton;
            return this;
        }

        @NonNull public Builder setNegativeButton(@StringRes int negativeButtonRes)
        {
            this.negativeButtonRes = negativeButtonRes;
            return this;
        }

        @NonNull public Builder setNeutralButton(@Nullable CharSequence neutralButton)
        {
            this.neutralButton = neutralButton;
            return this;
        }

        @NonNull public Builder setNeutralButton(@StringRes int neutralButtonRes)
        {
            this.neutralButtonRes = neutralButtonRes;
            return this;
        }

        @NonNull public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside)
        {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        @NonNull public Builder setSingleChoiceItems(@NonNull ListAdapter adapter, int checkedItem)
        {
            this.singleChoiceAdapter = adapter;
            this.singleChoiceCheckedItem = checkedItem;
            return this;
        }

        @NonNull public Builder setAlertDialogObserver(@Nullable Observer<AlertDialog> alertDialogObserver)
        {
            this.alertDialogObserver = alertDialogObserver;
            return this;
        }

        @NonNull public Observable<OnDialogClickEvent> build()
        {
            return Observable.create(new AlertDialogOnSubscribe(this))
                    .subscribeOn(AndroidSchedulers.mainThread());
        }
    }
}
