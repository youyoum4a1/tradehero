package com.tradehero.th.fragments.fxonboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.tradehero.th.R;
import rx.Observable;
import rx.android.observables.ViewObservable;

public class LearnMoreView extends LinearLayout
    implements FxOnBoardView<View>
{
    @Override public Observable<View> result()
    {
        return ViewObservable.clicks(findViewById(R.id.start_trading), false).asObservable();
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
