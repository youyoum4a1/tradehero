package com.tradehero.common.text;

/** Created with IntelliJ IDEA. User: tho Date: 9/18/13 Time: 2:54 PM Copyright (c) TradeHero */
public class RichTextMarker
{
    private int start;
    private int end;
    private Object span;

    public RichTextMarker(Object span, int start, int end)
    {
        this.start = start;
        this.end = end;
        this.span = span;
    }

    public Object getSpan()
    {
        return span;
    }

    public void setSpan(Object span)
    {
        this.span = span;
    }

    public int getStart()
    {
        return start;
    }

    public void setStart(int start)
    {
        this.start = start;
    }

    public int getEnd()
    {
        return end;
    }

    public void setEnd(int end)
    {
        this.end = end;
    }
}
