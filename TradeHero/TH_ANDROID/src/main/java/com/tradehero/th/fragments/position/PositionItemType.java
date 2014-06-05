package com.tradehero.th.fragments.position;

public enum PositionItemType
{
    Header(0),
    Placeholder(1),
    Locked(2),
    Open(3),
    OpenInPeriod(4),
    Closed(5),
    ClosedInPeriod(6);

    public final int value;

    private PositionItemType(int value)
    {
        this.value = value;
    }

    public static boolean takesPositionDTO(PositionItemType positionItemType)
    {
        switch (positionItemType)
        {
            case Header:
            case Placeholder:
            case Locked:
                return false;

            case Open:
            case OpenInPeriod:
            case Closed:
            case ClosedInPeriod:
                return true;

            default:
                throw new IllegalArgumentException("Unhandled value " + positionItemType);
        }
    }
}
