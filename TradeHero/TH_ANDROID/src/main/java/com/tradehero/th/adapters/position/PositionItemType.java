package com.tradehero.th.adapters.position;

/**
 * Created by julien on 31/10/13
 */
public enum PositionItemType
{
    Header(0),
    Placeholder(1),
    Locked(2),
    Open(3),
    Closed(4);

    public final int value;

    private PositionItemType(int value) {
        this.value = value;
    }
}
