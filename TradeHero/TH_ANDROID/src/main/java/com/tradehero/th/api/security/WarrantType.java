package com.tradehero.th.api.security;

import android.support.annotation.StringRes;
import com.tradehero.th.R;
import java.util.HashMap;
import java.util.Map;

public enum WarrantType
{
    CALL("C", R.string.warrant_type_call),
    PUT("P", R.string.warrant_type_put);

    private static Map<String, WarrantType> shortCodeMap;

    public final String shortCode;
    @StringRes public final int stringResId;

    WarrantType(String shortCode, int stringResId)
    {
        this.shortCode = shortCode;
        this.stringResId = stringResId;
    }

    private static Map<String, WarrantType> getShortCodeMap()
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

    public static WarrantType getByShortCode(String shortCode)
    {
        return getShortCodeMap().get(shortCode);
    }
}
