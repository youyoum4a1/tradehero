package com.tradehero.th.api.misc;

/** Created with IntelliJ IDEA. User: tho Date: 11/29/13 Time: 5:10 PM Copyright (c) TradeHero */
public enum ClientType
{
    Unknown(0),
    IOS(1),
    Android(2);

    private final int serverValue;

    ClientType(int serverValue)
    {
        this.serverValue = serverValue;
    }

    public int getServerValue()
    {
        return serverValue;
    }
}
