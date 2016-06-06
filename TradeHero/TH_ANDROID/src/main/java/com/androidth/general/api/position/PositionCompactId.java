package com.androidth.general.api.position;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.AbstractIntegerDTOKey;

public class PositionCompactId extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY_KEY = PositionCompactId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public PositionCompactId(Bundle args)
    {
        super(args);
    }

    public PositionCompactId(Integer key)
    {
        super(key);
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
