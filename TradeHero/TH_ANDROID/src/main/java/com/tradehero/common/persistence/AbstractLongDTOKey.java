package com.tradehero.common.persistence;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: xavier Date: 22/01/14 Time: 5:13 PM To change this template use File | Settings | File Templates. */

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
