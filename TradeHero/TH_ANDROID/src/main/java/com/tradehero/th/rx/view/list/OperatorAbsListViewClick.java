package com.tradehero.th.rx.view.list;

import android.widget.AbsListView;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.Assertions;
import rx.android.subscriptions.AndroidSubscriptions;

public class OperatorAbsListViewClick<T extends AbsListView>
        implements Observable.OnSubscribe<ItemClickDTO>
{
    @NotNull private final T view;

    //<editor-fold desc="Constructors">
    public OperatorAbsListViewClick(@NotNull T absListView)
    {
        this.view = absListView;
    }
    //</editor-fold>

    @Override
    public void call(final Subscriber<? super ItemClickDTO> observer)
    {
        Assertions.assertUiThread();
        final CompositeOnItemClickListener composite = CachedOnItemClickedListeners.getFromViewOrCreate(view);

        final AbsListView.OnItemClickListener listener =
                (parent, view1, position, id) -> observer.onNext(new ItemClickDTO(parent, view1, position, id));

        final Subscription subscription = AndroidSubscriptions.unsubscribeInUiThread(() -> composite.removeOnClickListener(listener));

        composite.addOnClickListener(listener);
        observer.add(subscription);
    }
}
