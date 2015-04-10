package com.tradehero.th.fragments.fxonboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tradehero.th.R;
import com.tradehero.th.rx.ReplaceWith;
import rx.Observable;
import rx.android.view.ViewObservable;

public class IntroductionView extends LinearLayout
    implements FxOnBoardView<Boolean>
{
    public IntroductionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @NonNull @Override public Observable<Boolean> result()
    {
        return ViewObservable.clicks(findViewById(R.id.next_button), false)
                .map(new ReplaceWith<>(true));
    }
}