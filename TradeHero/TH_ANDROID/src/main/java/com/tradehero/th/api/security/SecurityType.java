package com.tradehero.th.api.security;

import com.tradehero.thm.R;
import com.tradehero.th.api.security.compact.BondCompactDTO;
import com.tradehero.th.api.security.compact.CoveredWarrantDTO;
import com.tradehero.th.api.security.compact.DepositoryReceiptDTO;
import com.tradehero.th.api.security.compact.EquityCompactDTO;
import com.tradehero.th.api.security.compact.FundCompactDTO;
import com.tradehero.th.api.security.compact.PreferenceShareDTO;
import com.tradehero.th.api.security.compact.PreferredSecurityDTO;
import com.tradehero.th.api.security.compact.StapledSecurityDTO;
import com.tradehero.th.api.security.compact.TradableRightsIssueDTO;
import com.tradehero.th.api.security.compact.UnitCompactDTO;
import com.tradehero.th.api.security.compact.WarrantDTO;
import java.util.HashMap;
import java.util.Map;

public enum SecurityType
{
    EQUITY (R.string.security_type_equity, EquityCompactDTO.DTO_DESERIALISING_TYPE),
    FUND (R.string.security_type_fund, FundCompactDTO.DTO_DESERIALISING_TYPE),
    WARRANT (R.string.security_type_warrant, WarrantDTO.DTO_DESERIALISING_TYPE),
    BOND (R.string.security_type_bond, BondCompactDTO.DTO_DESERIALISING_TYPE),
    UNIT (R.string.security_type_unit, UnitCompactDTO.DTO_DESERIALISING_TYPE),
    TRADABLE_RIGHTS_ISSUE (R.string.security_type_tradable_rights_issue, TradableRightsIssueDTO.DTO_DESERIALISING_TYPE),
    PREFERENCE_SHARE (R.string.security_type_preference_share, PreferenceShareDTO.DTO_DESERIALISING_TYPE),
    DEPOSITORY_RECEIPTS (R.string.security_type_depository_receipt, DepositoryReceiptDTO.DTO_DESERIALISING_TYPE),
    COVERED_WARRANT (R.string.security_type_covered_warrant, CoveredWarrantDTO.DTO_DESERIALISING_TYPE),
    PREFERRED_SEC (R.string.security_type_preferred_sec, PreferredSecurityDTO.DTO_DESERIALISING_TYPE),
    STAPLED_SEC (R.string.security_type_stapled_sec, StapledSecurityDTO.DTO_DESERIALISING_TYPE);

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
