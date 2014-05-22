package com.tradehero.th.api.security;

import com.tradehero.th.R;
import java.util.HashMap;
import java.util.Map;

public enum SecurityType
{
    EQUITY (1, R.string.security_type_equity),
    FUND (2, R.string.security_type_fund),
    WARRANT (3, R.string.security_type_warrant),
    BOND (4, R.string.security_type_bond),
    UNIT (5, R.string.security_type_unit),
    TRADABLE_RIGHTS_ISSUE (6, R.string.security_type_tradable_rights_issue),
    PREFERENCE_SHARE (7, R.string.security_type_preference_share),
    DEPOSITORY_RECEIPTS (8, R.string.security_type_depository_receipt),
    COVERED_WARRANT (9, R.string.security_type_covered_warrant),
    PREFERRED_SEC (10, R.string.security_type_preferred_sec),
    STAPLED_SEC (11, R.string.security_type_stapled_sec);

    private static Map<Integer, SecurityType> valuesMap;

    public final int value;
    public final int stringResId;
    private SecurityType(int value, int stringResId)
    {
        this.value = value;
        this.stringResId = stringResId;
    }

    private static Map<Integer, SecurityType> getValuesMap()
    {
        if (valuesMap == null)
        {
            valuesMap = new HashMap<>();
            for (SecurityType securityType:SecurityType.values())
            {
                valuesMap.put(securityType.value, securityType);
            }
        }
        return valuesMap;
    }

    public static SecurityType getByValue(int value)
    {
        return getValuesMap().get(value);
    }
}
