package com.androidth.general.common.rx;

import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.PopupMenu;
import rx.Observable;
import rx.Subscriber;
import rx.android.internal.Assertions;

public class PopupMenuItemClickOperator implements Observable.OnSubscribe<MenuItem>
{
    @NonNull private final PopupMenu popupMenu;
    private final boolean eventHandled;

    public PopupMenuItemClickOperator(@NonNull PopupMenu popupMenu, boolean eventHandled)
    {
        this.popupMenu = popupMenu;
        this.eventHandled = eventHandled;
    }

    @Override public void call(final Subscriber<? super MenuItem> subscriber)
    {
        Assertions.assertUiThread();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override public boolean onMenuItemClick(MenuItem item)
            {
                subscriber.onNext(item);
                subscriber.onCompleted();
                return eventHandled;
            }
        });
        popupMenu.show();
    }
}
