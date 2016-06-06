package com.androidth.general.fragments.fxonboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.androidth.general.R;
import com.androidth.general.rx.ReplaceWithFunc1;
import rx.Observable;
import rx.android.view.ViewObservable;

public class ActivateTradeView extends LinearLayout
    implements FxOnBoardView<Boolean>
{
    public ActivateTradeView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @NonNull @Override public Observable<Boolean> result()
    {
        return ViewObservable.clicks(findViewById(R.id.start_trading), false)
                .map(new ReplaceWithFunc1<>(false));
    }
}
