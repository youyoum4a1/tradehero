package com.androidth.general.fragments.fxonboard;

import android.support.annotation.NonNull;
import rx.Observable;

public interface FxOnBoardView<T>
{
    @NonNull Observable<T> result();
}
