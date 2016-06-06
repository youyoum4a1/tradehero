package com.androidth.general.common.persistence;

import android.os.Bundle;
import android.support.annotation.NonNull;

public abstract class AbstractStringDTOKey extends AbstractPrimitiveDTOKey<String>
{
    //<editor-fold desc="Constructors">
    public AbstractStringDTOKey(@NonNull String key)
    {
        super(key);
    }

    public AbstractStringDTOKey(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @NonNull abstract public String getBundleKey();

    public void putParameters(@NonNull Bundle args)
    {
        args.putString(getBundleKey(), key);
    }
}
