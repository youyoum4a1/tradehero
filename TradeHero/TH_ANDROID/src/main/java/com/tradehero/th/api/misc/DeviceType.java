package com.tradehero.th.api.misc;


public enum DeviceType
{
    Unknown(0),
    IOS(1),
    Android(2),
    Baidu(3)
    ;

    private final int serverValue;

    DeviceType(int serverValue)
    {
        this.serverValue = serverValue;
    }

    public int getServerValue()
    {
        return serverValue;
    }
}
