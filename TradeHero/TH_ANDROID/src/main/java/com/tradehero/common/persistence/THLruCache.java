package com.tradehero.common.persistence;

import android.support.v4.util.LruCache;
import com.tradehero.common.utils.THLog;

/**
 * Created by xavier on 2/3/14.
 */
public class THLruCache<U, V> extends LruCache<U, V>
{
    public static final String TAG = THLruCache.class.getSimpleName();

    public THLruCache(int maxSize)
    {
        super(maxSize);
    }

    @Override protected void entryRemoved(boolean evicted, U key, V oldValue, V newValue)
    {
        super.entryRemoved(evicted, key, oldValue, newValue);
        if (evicted)
        {
            THLog.d(TAG, "entryRemoved evicted " + evicted + ", key " + key + ", oldValue " + oldValue + ", newValue " + newValue);
        }
    }
}
