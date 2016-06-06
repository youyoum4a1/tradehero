package com.androidth.general.common.persistence;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Use single integer as an identity
 */
abstract public class AbstractIntegerDTOKey extends AbstractPrimitiveDTOKey<Integer>
{
    //<editor-fold desc="Constructors">
    public AbstractIntegerDTOKey()
    {
        super();
    }

    public AbstractIntegerDTOKey(Integer key)
    {
        super(key);
    }

    public AbstractIntegerDTOKey(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @NonNull abstract public String getBundleKey();

    /**
     * If the key is null, it removes it from the bundle
     */
    public void putParameters(@NonNull Bundle args)
    {
        if (key == null)
        {
            args.remove(getBundleKey());
        }
        else
        {
            args.putInt(getBundleKey(), key);
        }
    }
}
