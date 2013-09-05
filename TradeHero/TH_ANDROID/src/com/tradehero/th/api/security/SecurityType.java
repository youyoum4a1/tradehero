package com.tradehero.th.api.security;

/** Created with IntelliJ IDEA. User: xavier Date: 9/4/13 Time: 5:31 PM To change this template use File | Settings | File Templates. */
public enum SecurityType
{
    EQUITY (1),
    FUND (2),
    WARRANT (3),
    BOND (4),
    UNIT (5),
    TRADABLE_RIGHTS_ISSUE (6),
    PREFERENCE_SHARE (7),
    DEPOSITORY_RECEIPTS (8),
    COVERED_WARRANT (9),
    PREFERRED_SEC (10),
    STAPLED_SEC (11);

    private final int value;
    private SecurityType(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
