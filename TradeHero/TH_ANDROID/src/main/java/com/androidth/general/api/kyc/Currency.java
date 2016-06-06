package com.androidth.general.api.kyc;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.androidth.general.R;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Currency
{
    GBP(R.string.currency_gbp, "GBP"),
    EUR(R.string.currency_eur, "EUR"),
    USD(R.string.currency_usd, "USD"),
    SEK(R.string.currency_sek, "SEK"),
    CHF(R.string.currency_chf, "CHF");

    private static final Map<String, Currency> filledCurrencies;

    @StringRes public final int dropDownText;
    @NonNull private final String fromServer;

    Currency(@StringRes int dropDownText, @NonNull String fromServer)
    {
        this.dropDownText = dropDownText;
        this.fromServer = fromServer;
    }

    @SuppressWarnings("unused")
    @JsonCreator @NonNull static Currency getCurrency(@NonNull String fromServer)
    {
        Currency candidate = filledCurrencies.get(fromServer);
        if (candidate == null)
        {
            throw new IllegalArgumentException(fromServer + " does not match any Currency");
        }
        return candidate;
    }

    static
    {
        Map<String, Currency> map = new HashMap<>();
        for (Currency candidate : values())
        {
            if (map.get(candidate.fromServer) != null)
            {
                throw new IllegalArgumentException("You are not allowed to add '" + candidate.fromServer + "' a second time");
            }
            map.put(candidate.fromServer, candidate);
        }
        filledCurrencies = Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unused")
    @JsonValue @NonNull String getFromServerCode()
    {
        return fromServer;
    }
}
