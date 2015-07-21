package com.tradehero.th.models.fastfill.jumio;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tradehero.th.models.fastfill.ScanReference;

public class NetverifyScanReference implements ScanReference
{
    @NonNull private final String value;

    @JsonCreator public NetverifyScanReference(@NonNull String value)
    {
        this.value = value;
    }

    @JsonValue @NonNull @Override public String getValue()
    {
        return value;
    }
}
