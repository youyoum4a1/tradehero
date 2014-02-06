package com.tradehero.common.persistence.prefs;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/6/14 Time: 11:25 AM Copyright (c) TradeHero
 */
public interface TypePreference<T>
{
    T get();
    void set(T value);
    void delete();
    boolean isSet();
}
