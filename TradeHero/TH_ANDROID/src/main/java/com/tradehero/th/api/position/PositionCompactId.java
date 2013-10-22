package com.tradehero.th.api.position;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 12:29 PM To change this template use File | Settings | File Templates. */
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

    @Override public boolean equals(Object other)
    {
        return (other instanceof PositionCompactId) && equals((PositionCompactId) other);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }
}
