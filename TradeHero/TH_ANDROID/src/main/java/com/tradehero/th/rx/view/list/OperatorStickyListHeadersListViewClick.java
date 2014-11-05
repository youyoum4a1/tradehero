package com.tradehero.th.rx.view.list;

import android.widget.AbsListView;
import android.support.annotation.NonNull;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.Assertions;
import rx.android.subscriptions.AndroidSubscriptions;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class OperatorStickyListHeadersListViewClick<T extends StickyListHeadersListView>
        implements Observable.OnSubscribe<ItemClickDTO>
{
    @NonNull private final T view;

    //<editor-fold desc="Constructors">
    public OperatorStickyListHeadersListViewClick(@NonNull T absListView)
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
