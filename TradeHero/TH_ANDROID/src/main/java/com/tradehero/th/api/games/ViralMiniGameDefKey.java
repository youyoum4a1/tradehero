package com.tradehero.th.api.games;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.route.RouteProperty;

public class ViralMiniGameDefKey extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = ViralMiniGameDefKey.class.getName() +".key";

    //<editor-fold desc="Constructors">
    public ViralMiniGameDefKey(Integer key)
    {
        super(key);
    }

    public ViralMiniGameDefKey(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }

    @Override public String toString()
    {
        return String.format("[ViralMiniGameId key=%d]", key);
    }
}
