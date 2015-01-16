package com.tradehero.th.api.security;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.tradehero.th.R;
import java.util.HashMap;
import java.util.Map;

public enum WarrantType
{
    CALL("C", R.string.warrant_type_call),
    PUT("P", R.string.warrant_type_put);

    private static Map<String, WarrantType> shortCodeMap;

    @NonNull public final String shortCode;
    @StringRes public final int stringResId;

    //<editor-fold desc="Constructors">
    WarrantType(@NonNull String shortCode, int stringResId)
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

    @Nullable public static WarrantType getByShortCode(@NonNull String shortCode)
    {
        return getShortCodeMap().get(shortCode);
    }
}
