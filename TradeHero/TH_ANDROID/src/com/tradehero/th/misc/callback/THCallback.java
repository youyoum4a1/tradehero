package com.tradehero.th.misc.callback;

import com.tradehero.th.misc.exception.THException;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 5:18 PM Copyright (c) TradeHero */
public interface THCallback<T>
{
    void internalDone(T paramT, THException ex);
}
