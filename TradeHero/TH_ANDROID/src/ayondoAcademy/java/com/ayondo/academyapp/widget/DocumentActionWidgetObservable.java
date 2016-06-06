package com.ayondo.academyapp.widget;

import android.support.annotation.NonNull;
import android.view.View;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class DocumentActionWidgetObservable
{
    @NonNull public static Observable<DocumentActionWidgetAction> actions(@NonNull final DocumentActionWidget widget)
    {
        return Observable.create(new Observable.OnSubscribe<DocumentActionWidgetAction>()
        {
            @Override public void call(final Subscriber<? super DocumentActionWidgetAction> subscriber)
            {
                widget.setActionOnClickListener(new View.OnClickListener()
                {
                    @Override public void onClick(View v)
                    {
                        subscriber.onNext(new DocumentActionWidgetAction(v, DocumentActionWidgetActionType.ACTION));
                    }
                });
                widget.setClearOnClickListener(new View.OnClickListener()
                {
                    @Override public void onClick(View v)
                    {
                        subscriber.onNext(new DocumentActionWidgetAction(v, DocumentActionWidgetActionType.CLEAR));
                    }
                });
                subscriber.add(Subscriptions.create(new Action0()
                {
                    @Override public void call()
                    {
                        widget.setActionOnClickListener(null);
                        widget.setClearOnClickListener(null);
                    }
                }));
            }
        });
    }
}
