package com.androidth.general.fragments.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import butterknife.ButterKnife;
import com.androidth.general.R;
import com.androidth.general.inject.HierarchyInjector;
import rx.Subscription;
import rx.internal.util.SubscriptionList;

public abstract class BaseDialogSupportFragment extends DialogFragment
{
    private OnDismissedListener dismissedListener;
    @NonNull protected SubscriptionList onStopSubscriptions;

    @Override @NonNull public Dialog onCreateDialog(@NonNull Bundle savedInstanceState)
    {
        Dialog d = super.onCreateDialog(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.TH_Dialog);
        d.setCanceledOnTouchOutside(shouldCancelOnOutsideClicked());
        return d;
    }

    protected boolean shouldCancelOnOutsideClicked()
    {
        return true;
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override public void onStart()
    {
        super.onStart();
        onStopSubscriptions = new SubscriptionList();
    }

    @Override public void onStop()
    {
        onStopSubscriptions.unsubscribe();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    public void setDismissedListener(OnDismissedListener dismissedListener)
    {
        this.dismissedListener = dismissedListener;
    }

    @Override public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        notifyDismissed(dialog);
    }

    protected void notifyDismissed(DialogInterface dialog)
    {
        OnDismissedListener dismissedListenerCopy = dismissedListener;
        if (dismissedListenerCopy != null)
        {
            dismissedListenerCopy.onDismissed(dialog);
        }
        dismissedListener = null;
    }

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    public static interface OnDismissedListener
    {
        void onDismissed(DialogInterface dialog);
    }
}
