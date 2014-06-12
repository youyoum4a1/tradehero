package com.tradehero.common.persistence;

import android.os.Bundle;
import org.jetbrains.annotations.NotNull;

/**
 * Use single integer as an identity
 */
abstract public class AbstractIntegerDTOKey extends AbstractPrimitiveDTOKey<Integer>
{
    //<editor-fold desc="Constructors">
    public AbstractIntegerDTOKey(Integer key)
    {
        super(key);
    }

    public AbstractIntegerDTOKey(@NotNull Bundle args)
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
            args.putInt(getBundleKey(), key);
        }
    }
}
