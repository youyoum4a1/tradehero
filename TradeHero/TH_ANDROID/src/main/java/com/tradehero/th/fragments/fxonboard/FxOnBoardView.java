package com.tradehero.th.fragments.fxonboard;

import rx.Observable;

public interface FxOnBoardView<T>
{
    Observable<T> result();
}
