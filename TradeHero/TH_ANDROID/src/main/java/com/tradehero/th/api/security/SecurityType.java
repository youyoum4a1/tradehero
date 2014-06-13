package com.tradehero.th.api.security;

import com.tradehero.th.R;
import java.util.HashMap;
import java.util.Map;

public enum SecurityType
{
    EQUITY (R.string.security_type_equity, SecurityCompactDTO.EQUITY_DTO_DESERIALISING_TYPE),
    FUND (R.string.security_type_fund, SecurityCompactDTO.FUND_DTO_DESERIALISING_TYPE),
    WARRANT (R.string.security_type_warrant, WarrantDTO.WARRANT_DTO_DESERIALISING_TYPE),
    BOND (R.string.security_type_bond, SecurityCompactDTO.BOND_DTO_DESERIALISING_TYPE),
    UNIT (R.string.security_type_unit, SecurityCompactDTO.UNIT_DTO_DESERIALISING_TYPE),
    TRADABLE_RIGHTS_ISSUE (R.string.security_type_tradable_rights_issue, SecurityCompactDTO.TRADABLE_RIGHTS_ISSUE_DTO_DESERIALISING_TYPE),
    PREFERENCE_SHARE (R.string.security_type_preference_share, SecurityCompactDTO.PREFERENCE_SHARE_DTO_DESERIALISING_TYPE),
    DEPOSITORY_RECEIPTS (R.string.security_type_depository_receipt, SecurityCompactDTO.DEPOSITORY_RECEIPTS_DTO_DESERIALISING_TYPE),
    COVERED_WARRANT (R.string.security_type_covered_warrant, SecurityCompactDTO.COVERED_WARRANT_DTO_DESERIALISING_TYPE),
    PREFERRED_SEC (R.string.security_type_preferred_sec, SecurityCompactDTO.PREFERRED_SEC_DTO_DESERIALISING_TYPE),
    STAPLED_SEC (R.string.security_type_stapled_sec, SecurityCompactDTO.STAPLED_SEC_DTO_DESERIALISING_TYPE);

    private static Map<Integer, SecurityType> valuesMap;

    public final int value;
    public final int stringResId;
    public final String deserialisingId;

    //<editor-fold desc="Constructors">
    SecurityType(int stringResId, String deserialisingId)
    {
        this(Integer.valueOf(deserialisingId), stringResId, deserialisingId);
    }

    private SecurityType(int value, int stringResId, String deserialisingId)
    {
        this.value = value;
        this.stringResId = stringResId;
        this.deserialisingId = deserialisingId;
    }
    //</editor-fold>

    private static Map<Integer, SecurityType> getValuesMap()
    {
        if (valuesMap == null)
        {
            valuesMap = new HashMap<>();
            for (SecurityType securityType:SecurityType.values())
            {
                if (valuesMap.containsKey(securityType.value))
                {
                    throw new IllegalArgumentException(String.format("SecurityType.%s has same value as another one", securityType));
                }
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
