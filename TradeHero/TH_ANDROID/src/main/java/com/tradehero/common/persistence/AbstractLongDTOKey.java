package com.tradehero.common.persistence;

import android.os.Bundle;

/**
 * Use single long as an identity
 */
abstract public class AbstractLongDTOKey extends AbstractPrimitiveDTOKey<Long>
{
    //<editor-fold desc="Constructors">
    public AbstractLongDTOKey(Long key)
    {
        super(key);
    }

    public AbstractLongDTOKey(Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    abstract public String getBundleKey();

    /**
     * If the key is null, it removes it from the bundle
     * @param args
     */
    public void putParameters(Bundle args)
    {
        if (key == null)
        {
            args.remove(getBundleKey());
        }
        else
        {
            args.putLong(getBundleKey(), key);
        }
    }
}
