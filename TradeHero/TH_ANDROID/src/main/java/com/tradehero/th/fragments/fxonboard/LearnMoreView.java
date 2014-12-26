package com.tradehero.th.fragments.fxonboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tradehero.th.R;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.ViewObservable;
import rx.subjects.PublishSubject;

public class LearnMoreView extends LinearLayout
    implements FxOnBoardView<Boolean>
{
    private PublishSubject<Boolean> resultSubject = PublishSubject.create();
    private Subscription enrollmentSubscription;

    @Override public Observable<Boolean> result()
    {
        return resultSubject.asObservable();
    }

    public LearnMoreView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        enrollmentSubscription = ViewObservable.clicks(findViewById(R.id.next_button), false)
                //.flatMap(view -> fxService.enroll()) TODO when server ready, implement this
                .map(view -> true) // continue to next screen
                .subscribe(resultSubject);
    }

    @Override protected void onDetachedFromWindow()
    {
        enrollmentSubscription.unsubscribe();
        super.onDetachedFromWindow();
    }
}
