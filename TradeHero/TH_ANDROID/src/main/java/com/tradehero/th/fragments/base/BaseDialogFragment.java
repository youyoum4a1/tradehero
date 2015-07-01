package com.tradehero.th.fragments.base;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import butterknife.ButterKnife;
import com.tradehero.th.R;
import com.tradehero.th.inject.HierarchyInjector;
import rx.Observable;
import rx.Subscription;
import rx.internal.util.SubscriptionList;
import rx.subjects.BehaviorSubject;

public abstract class BaseDialogFragment extends DialogFragment
{
    private BehaviorSubject<DialogInterface> dismissedSubject;
    @NonNull protected SubscriptionList onStopSubscriptions;

    //<editor-fold desc="Constructors">
    public BaseDialogFragment()
    {
        super();
        dismissedSubject = BehaviorSubject.create();
    }
    //</editor-fold>

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

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        onStopSubscriptions = new SubscriptionList();
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
    }

    @Override public void onStop()
    {
        onStopSubscriptions.unsubscribe();
        onStopSubscriptions = new SubscriptionList();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        notifyDismissed(dialog);
    }

    @NonNull public Observable<DialogInterface> getDismissedObservable()
    {
        return dismissedSubject.asObservable();
    }

    protected void notifyDismissed(DialogInterface dialog)
    {
        dismissedSubject.onNext(dialog);
        dismissedSubject.onCompleted();
    }

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }
}
