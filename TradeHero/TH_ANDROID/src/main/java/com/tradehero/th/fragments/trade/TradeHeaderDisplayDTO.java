package com.tradehero.th.fragments.trade;

public class TradeHeaderDisplayDTO
{
    public final static int HEADER_TYPE_POSITION = 0;
    public final static int HEADER_TYPE_TRADE = 1;

    public final int headerType;
    public final String header;

    public TradeHeaderDisplayDTO(int headerType, String header)
    {
        this.headerType = headerType;
        this.header = header;
    }
}
