package com.tradehero.th.fragments.fxonboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.tradehero.th.R;
import rx.Observable;
import rx.android.observables.ViewObservable;

public class IntroductionView extends LinearLayout
    implements FxOnBoardView<View>
{
    @Override public Observable<View> result()
    {
        return ViewObservable.clicks(findViewById(R.id.next), false).asObservable();
    }

    public IntroductionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }
}
