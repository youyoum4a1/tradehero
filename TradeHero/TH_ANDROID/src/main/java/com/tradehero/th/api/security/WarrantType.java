package com.tradehero.th.api.security;

import java.util.HashMap;
import java.util.Map;

public enum WarrantType
{
    CALL("C"),
    PUT("P");

    private static Map<String, WarrantType> shortCodeMap;

    public final String shortCode;

    WarrantType(String shortCode)
    {
        this.shortCode = shortCode;
    }

    private static Map<String, WarrantType> getShortCodeMap()
    {
        if (shortCodeMap == null)
        {
            shortCodeMap = new HashMap<>();
            for (WarrantType warrantType:WarrantType.values())
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
