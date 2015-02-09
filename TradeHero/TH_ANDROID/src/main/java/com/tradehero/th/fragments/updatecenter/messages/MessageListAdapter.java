package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import java.util.Collection;
import java.util.Comparator;
import rx.Observable;
import rx.internal.util.SubscriptionList;
import rx.subjects.BehaviorSubject;

public class MessageListAdapter extends ViewDTOSetAdapter<MessageHeaderDTO, MessageItemViewWrapper>
{
    @LayoutRes private final int layoutResourceId;
    @NonNull private BehaviorSubject<MessageItemView.UserAction> userActionBehavior;
    @NonNull private SubscriptionList subscriptions;

    //<editor-fold desc="Constructors">
    public MessageListAdapter(
            @NonNull Context context,
            @Nullable Collection<MessageHeaderDTO> objects,
            @LayoutRes int layoutResourceId,
            @Nullable Comparator<MessageHeaderDTO> comparator)
    {
        super(context, comparator, objects);
        this.layoutResourceId = layoutResourceId;
        userActionBehavior = BehaviorSubject.create();
        subscriptions = new SubscriptionList();
    }
    //</editor-fold>

    public void onDestroy()
    {
        subscriptions.unsubscribe();
    }

    @Override public MessageItemViewWrapper getView(int position, View convertView, ViewGroup parent)
    {
        MessageItemViewWrapper view = super.getView(position, convertView, parent);
        if (convertView == null)
        {
            subscriptions.add(view.getUserActionObservable().subscribe(userActionBehavior));
        }
        return view;
    }

    @Override @LayoutRes protected int getViewResId(int position)
    {
        return layoutResourceId;
    }

    @NonNull public Observable<MessageItemView.UserAction> getUserActionObservable()
    {
        return userActionBehavior.asObservable();
    }
}
