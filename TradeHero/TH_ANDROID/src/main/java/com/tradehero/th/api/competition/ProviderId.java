package com.ayondo.academy.api.competition;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.route.RouteProperty;

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

    public ProviderId(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @RouteProperty("providerId")
    public void setKey(Integer key)
    {
        this.key = key;
    }

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public String toString()
    {
        return String.format("[ProviderId key=%d]", key);
    }
}
