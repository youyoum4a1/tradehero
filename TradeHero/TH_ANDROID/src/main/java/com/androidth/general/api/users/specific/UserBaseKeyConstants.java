package com.androidth.general.api.users.specific;

public class UserBaseKeyConstants
{
    public static final int OFFICIAL_TRADEHERO = 562001;
    public static final int OFFICIAL_COMMUNITY_MANAGER = 562005;
    public static final int OFFICIAL_TRADE_MASTER = 562018;
    public static final int OFFICIAL_ACCOUNT_4 = 570750;
    public static final int OFFICIAL_ACCOUNT_5 = 570758;
    public static final int OFFICIAL_ACCOUNT_6 = 570762;

    public static boolean isOfficialId(int id)
    {
        return id == OFFICIAL_TRADEHERO
                || id == OFFICIAL_COMMUNITY_MANAGER
                || id == OFFICIAL_TRADE_MASTER
                || id == OFFICIAL_ACCOUNT_4
                || id == OFFICIAL_ACCOUNT_5
                || id == OFFICIAL_ACCOUNT_6;
    }
}
