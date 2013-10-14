package com.tradehero.common.persistence;

import android.os.Bundle;

/** Created with IntelliJ IDEA. User: xavier Date: 10/10/13 Time: 5:13 PM To change this template use File | Settings | File Templates. */
abstract public class AbstractIntegerDTOKey extends AbstractPrimitiveDTOKey<Integer>
{
    //<editor-fold desc="Constructors">
    public AbstractIntegerDTOKey(Integer key)
    {
        super(key);
    }

    public AbstractIntegerDTOKey(Bundle args)
    {
        super(args);
    }


    abstract public String getBundleKey();


    public void putParameters(Bundle args)
    {
        args.putInt(getBundleKey(), key);
    }
}
