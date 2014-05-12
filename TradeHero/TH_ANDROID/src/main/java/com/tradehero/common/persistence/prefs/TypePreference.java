package com.tradehero.common.persistence.prefs;


public interface TypePreference<T>
{
    T get();
    void set(T value);
    void delete();
    boolean isSet();
}
