package com.tradehero.th.fragments.fxonboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tradehero.th.R;
import rx.Observable;
import rx.android.observables.ViewObservable;

public class ActivateTradeView extends LinearLayout
    implements FxOnBoardView<Boolean>
{
    @NonNull @Override public Observable<Boolean> result()
    {
        return ViewObservable.clicks(findViewById(R.id.start_trading), false)
                .map(t -> false)
                .asObservable();
    }

    public ActivateTradeView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
}
