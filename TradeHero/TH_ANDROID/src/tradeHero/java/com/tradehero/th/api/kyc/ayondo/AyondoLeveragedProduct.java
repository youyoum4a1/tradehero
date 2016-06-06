package com.androidth.general.api.kyc.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.tradehero.th.R;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum AyondoLeveragedProduct
{
    OTC_DERIVATE(R.string.experience_otc_derivatives, "OTC Derivatives"),
    EXCHANGE_TRADED_DERIVATIVE(R.string.experience_exchange_traded_derivatives, "Exchange Traded Derivatives"),
    SHARE_AND_BOND(R.string.experience_bonds, "Shares and Bonds");

    private static final Map<String, AyondoLeveragedProduct> filedLeveragedProductes;

    @StringRes public final int dropDownText;
    @NonNull private final String fromServer;

    AyondoLeveragedProduct(@StringRes int dropDownText, @NonNull String fromServer)
    {
        this.dropDownText = dropDownText;
        this.fromServer = fromServer;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static AyondoLeveragedProduct getLeveragedProduct(@NonNull String fromServer)
    {
        AyondoLeveragedProduct candidate = filedLeveragedProductes.get(fromServer);
        if (candidate == null)
        {
            throw new IllegalArgumentException(fromServer + " does not match any LeveragedProduct");
        }
        return candidate;
    }

    static
    {
        Map<String, AyondoLeveragedProduct> map = new HashMap<>();
        for (AyondoLeveragedProduct candidate : values())
        {
            if (map.get(candidate.fromServer) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.fromServer + "' a second time");
            }
            map.put(candidate.fromServer, candidate);
        }
        filedLeveragedProductes = Collections.unmodifiableMap(map);
    }

    @JsonValue @NonNull @Override public String toString()
    {
        return fromServer;
    }
}
