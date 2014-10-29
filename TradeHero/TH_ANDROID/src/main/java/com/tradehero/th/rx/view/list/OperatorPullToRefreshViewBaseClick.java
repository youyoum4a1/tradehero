package com.tradehero.th.rx.view.list;

import android.view.View;
import android.widget.AbsListView;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import java.util.Map;
import java.util.WeakHashMap;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.Assertions;
import rx.android.subscriptions.AndroidSubscriptions;

public class OperatorPullToRefreshViewBaseClick<
        S extends AbsListView,
        T extends PullToRefreshAdapterViewBase<S>>
        implements Observable.OnSubscribe<ItemClickDTO>
{
    @NotNull private final T view;

    //<editor-fold desc="Constructors">
    public OperatorPullToRefreshViewBaseClick(@NotNull T absLitView)
    {
        this.view = absLitView;
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
