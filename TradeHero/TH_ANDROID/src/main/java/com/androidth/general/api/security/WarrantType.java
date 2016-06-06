package com.androidth.general.api.security;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.androidth.general.R;
import com.androidth.general.api.security.compact.WarrantDTO;
import java.util.HashMap;
import java.util.Map;

public enum WarrantType
{
    CALL(WarrantDTO.CALL_SHORT_CODE, R.string.warrant_type_call),
    PUT(WarrantDTO.PUT_SHORT_CODE, R.string.warrant_type_put);

    private static Map<String, WarrantType> shortCodeMap;

    @NonNull @WarrantTypeShortCode public final String shortCode;
    @StringRes public final int stringResId;

    //<editor-fold desc="Constructors">
    WarrantType(@NonNull @WarrantTypeShortCode String shortCode, int stringResId)
    {
        this.shortCode = shortCode;
        this.stringResId = stringResId;
    }
    //</editor-fold>

    @NonNull private static Map<String, WarrantType> getShortCodeMap()
    {
        if (shortCodeMap == null)
        {
            shortCodeMap = new HashMap<>();
            for (WarrantType warrantType : WarrantType.values())
            {
                shortCodeMap.put(warrantType.shortCode, warrantType);
            }
        }
        return shortCodeMap;
    }

    @Nullable public static WarrantType getByShortCode(@NonNull @WarrantTypeShortCode String shortCode)
    {
        return getShortCodeMap().get(shortCode);
    }
}
