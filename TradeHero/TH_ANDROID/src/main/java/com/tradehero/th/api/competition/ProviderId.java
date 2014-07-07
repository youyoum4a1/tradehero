package com.tradehero.th.api.competition;

import android.os.Bundle;
import com.tradehero.route.RouteProperty;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import org.jetbrains.annotations.NotNull;

public class ProviderId extends AbstractIntegerDTOKey
{
    public final static String BUNDLE_KEY_KEY = ProviderId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public ProviderId()
    {
        super();
    }

    public ProviderId(Integer key)
    {
        super(key);
    }

    public ProviderId(@NotNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @RouteProperty("providerId")
    public void setKey(Integer key)
    {
        this.key = key;
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public String toString()
    {
        return String.format("[ProviderId key=%d]", key);
    }
}
