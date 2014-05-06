package com.tradehero.th.api.position;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;


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

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
