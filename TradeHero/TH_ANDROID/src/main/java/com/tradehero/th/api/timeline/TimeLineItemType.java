package com.tradehero.th.api.timeline;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 7:21 PM To change this template use File | Settings | File Templates. */
public enum TimeLineItemType
{
    TLI_TRADE(1),
    TLI_COMMENT(2),
    TLI_TRADE_AND_COMMENT(3),
    TLI_CLOSED_POSITION(4),
    TLI_PUSH_ECHO(5),
    TLI_STARTED_FOLLOWING(6),
    TLI_STOCK_ALERT_EVENT(7),
    TLI_JOIN_COMPETITION(8);

    public final int value;

    private TimeLineItemType(int value)
    {
        this.value = value;
    }
}
