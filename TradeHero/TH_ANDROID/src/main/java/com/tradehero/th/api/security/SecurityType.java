package com.tradehero.th.api.security;

import com.tradehero.common.localisation.Translatable;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 9/4/13 Time: 5:31 PM To change this template use File | Settings | File Templates. */
public enum SecurityType
{
    @Translatable(stringResourceId = R.string.security_type_equity)
    EQUITY (1),

    @Translatable(stringResourceId = R.string.security_type_fund)
    FUND (2),

    @Translatable(stringResourceId = R.string.security_type_warrant)
    WARRANT (3),

    @Translatable(stringResourceId = R.string.security_type_bond)
    BOND (4),

    @Translatable(stringResourceId = R.string.security_type_unit)
    UNIT (5),

    @Translatable(stringResourceId = R.string.security_type_tradable_rights_issue)
    TRADABLE_RIGHTS_ISSUE (6),

    @Translatable(stringResourceId = R.string.security_type_preference_share)
    PREFERENCE_SHARE (7),

    @Translatable(stringResourceId = R.string.security_type_depository_receipt)
    DEPOSITORY_RECEIPTS (8),

    @Translatable(stringResourceId = R.string.security_type_covered_warrant)
    COVERED_WARRANT (9),

    @Translatable(stringResourceId = R.string.security_type_preferred_sec)
    PREFERRED_SEC (10),

    @Translatable(stringResourceId = R.string.security_type_stapled_sec)
    STAPLED_SEC (11);

    private static final String TAG = SecurityType.class.getSimpleName();
    private static Map<Integer, SecurityType> valuesMap;

    private final int value;
    private SecurityType(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    private static Map<Integer, SecurityType> getValuesMap()
    {
        if (valuesMap == null)
        {
            valuesMap = new HashMap<>();
            for(SecurityType securityType:SecurityType.values())
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

    public static int getStringResourceId(SecurityType securityType)
    {
        return getStringResourceId(securityType.name());
    }

    public static int getStringResourceId(String securityType)
    {
        try
        {
            return SecurityType.class.getField(securityType).getAnnotation(Translatable.class).stringResourceId();
        }
        catch (NoSuchFieldException e)
        {
            THLog.e(TAG, "Unavailable SecurityType name " + securityType, e);
            return 0;
        }
    }
}
