package com.tradehero.th.fragments.live;

import android.support.annotation.NonNull;
import rx.Observable;

public interface PrevNextObservable
{
    @NonNull Observable<Boolean> getPrevNextObservable();
}