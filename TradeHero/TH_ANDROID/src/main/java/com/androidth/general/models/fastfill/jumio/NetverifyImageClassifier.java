package com.androidth.general.models.fastfill.jumio;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class NetverifyImageClassifier
{
    @NonNull private final String value;

    @JsonCreator public NetverifyImageClassifier(@NonNull String value)
    {
        this.value = value;
    }

    @JsonValue @NonNull public String getValue()
    {
        return value;
    }
}
