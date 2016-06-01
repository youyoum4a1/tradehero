package com.ayondo.academy.models.fastfill.jumio;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ayondo.academy.models.fastfill.ScanReference;

public class NetverifyScanReference implements ScanReference
{
    public static final String TYPE = "netverify";

    @NonNull private final String value;

    public NetverifyScanReference(@JsonProperty("value") @NonNull String value)
    {
        this.value = value;
    }

    @NonNull @Override public String getValue()
    {
        return value;
    }

    @Override public int hashCode()
    {
        return value.hashCode();
    }

    @Override public boolean equals(Object o)
    {
        if (o == null) return false;
        if (o == this) return true;
        return o instanceof NetverifyScanReference
                && value.equals(((NetverifyScanReference) o).value);
    }
}
