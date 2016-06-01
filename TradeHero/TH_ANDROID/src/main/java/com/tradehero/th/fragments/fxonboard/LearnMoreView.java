package com.ayondo.academy.fragments.fxonboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.ayondo.academy.R;
import com.ayondo.academy.rx.ReplaceWithFunc1;
import rx.Observable;
import rx.android.view.ViewObservable;

public class LearnMoreView extends LinearLayout
    implements FxOnBoardView<Boolean>
{
    public LearnMoreView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @NonNull @Override public Observable<Boolean> result()
    {
        return ViewObservable.clicks(findViewById(R.id.next_button), false)
                .map(new ReplaceWithFunc1<>(true));
    }
}
