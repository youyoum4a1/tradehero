package com.androidth.general.common.persistence;

import android.util.LruCache;
import timber.log.Timber;

public class THLruCache<U, V> extends LruCache<U, V>
{
    public THLruCache(int maxSize)
    {
        super(maxSize);
    }

    @Override protected void entryRemoved(boolean evicted, U key, V oldValue, V newValue)
    {
        super.entryRemoved(evicted, key, oldValue, newValue);
        if (evicted)
        {
            Timber.d("entryRemoved evicted key %s, oldValue %s, newValue %s",
                    key.toString(),
                    oldValue.toString(),
                    newValue != null ? newValue.toString() : null);
        }
    }
}
