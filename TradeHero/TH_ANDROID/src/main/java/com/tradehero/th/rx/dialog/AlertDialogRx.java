package com.tradehero.th.rx.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Pair;
import android.view.View;
import android.widget.ListAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class AlertDialogRx
{
    @NonNull public static Builder build(@NonNull Context activityContext)
    {
        return new Builder(activityContext);
    }

    public static class Builder
    {
        boolean cancelable;
        @Nullable @DrawableRes Integer iconRes;
        @Nullable Drawable icon;
        @Nullable @StringRes Integer titleRes;
        @Nullable String title;
        @Nullable @StringRes Integer messageRes;
        @Nullable String message;
        @Nullable View view;
        @Nullable CharSequence positiveButton;
        @Nullable @StringRes Integer positiveButtonRes;
        @Nullable CharSequence negativeButton;
        @Nullable @StringRes Integer negativeButtonRes;
        @Nullable CharSequence neutralButton;
        @Nullable @StringRes Integer neutralButtonRes;
        boolean canceledOnTouchOutside;
        @Nullable ListAdapter singleChoiceAdapter;
        int singleChoiceCheckedItem;

        @NonNull final Context activityContext;

        //<editor-fold desc="Constructors">
        public Builder(@NonNull Context activityContext)
        {
            this.activityContext = activityContext;
        }
        //</editor-fold>

        @NonNull public Builder setCancelable(boolean cancelable)
        {
            this.cancelable = cancelable;
            return this;
        }

        @NonNull public Builder setIcon(@DrawableRes int iconRes)
        {
            this.iconRes = iconRes;
            return this;
        }

        @NonNull public Builder setIcon(@Nullable Drawable icon)
        {
            this.icon = icon;
            return this;
        }

        @NonNull public Builder setTitle(@StringRes int titleRes)
        {
            this.titleRes = titleRes;
            return this;
        }

        @NonNull public Builder setTitle(@Nullable String title)
        {
            this.title = title;
            return this;
        }

        @NonNull public Builder setMessage(@StringRes int messageRes)
        {
            this.messageRes = messageRes;
            return this;
        }

        @NonNull public Builder setMessage(@Nullable String message)
        {
            this.message = message;
            return this;
        }

        @NonNull public Builder setView(@Nullable View view)
        {
            this.view = view;
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

        @NonNull public Observable<OnDialogClickEvent> build()
        {
            return Observable.create(new AlertDialogOnSubscribe(this))
                    .subscribeOn(AndroidSchedulers.mainThread());
        }
    }
}
