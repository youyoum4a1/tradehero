package com.tradehero.th.fragments.position;

public enum PositionItemType
{
    Header(0),
    Placeholder(1),
    Locked(2),
    Open(3),
    Closed(4);

    public final int value;

    private PositionItemType(int value)
    {
        this.value = value;
    }
}
