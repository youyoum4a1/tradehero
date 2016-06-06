package com.androidth.general.api.kyc.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.androidth.general.R;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum AyondoProductType
{
    CFD(R.string.product_type_cfd, "CFD"),
    SPREAD_BETTING(R.string.product_type_spread_betting, "SpreadBetting");

    private static final Map<String, AyondoProductType> filedAyondoProductTypees;

    @StringRes public final int dropDownText;
    @NonNull private final String fromServer;

    AyondoProductType(@StringRes int dropDownText, @NonNull String fromServer)
    {
        this.dropDownText = dropDownText;
        this.fromServer = fromServer;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static AyondoProductType getAyondoProductType(@NonNull String fromServer)
    {
        AyondoProductType candidate = filedAyondoProductTypees.get(fromServer);
        if (candidate == null)
        {
            throw new IllegalArgumentException(fromServer + " does not match any AyondoProductType");
        }
        return candidate;
    }

    static
    {
        Map<String, AyondoProductType> map = new HashMap<>();
        for (AyondoProductType candidate : values())
        {
            if (map.get(candidate.fromServer) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.fromServer + "' a second time");
            }
            map.put(candidate.fromServer, candidate);
        }
        filedAyondoProductTypees = Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unused")
    @JsonValue @NonNull String getFromServerCode()
    {
        return fromServer;
    }
}
