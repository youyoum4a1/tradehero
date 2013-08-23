package com.tradehero.th.misc.exception;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 5:20 PM Copyright (c) TradeHero */
public class THException extends Exception
{
    private final int code;

    public THException(Throwable cause)
    {
        super(cause);
        this.code = -1;
    }

    public THException(String message)
    {
        super(new Exception(message));
        this.code = -1;
    }
}
