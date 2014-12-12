package com.tradehero.th.fragments.fxonboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tradehero.th.R;
import rx.Observable;
import rx.android.observables.ViewObservable;

public class LearnMoreView extends LinearLayout
    implements FxOnBoardView<Boolean>
{
    @Override public Observable<Boolean> result()
    {
        return ViewObservable.clicks(findViewById(R.id.next_button), false)
                .map(t -> true)
                .asObservable();
    }

    public LearnMoreView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
    }
}
