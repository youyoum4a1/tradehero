package com.tradehero.th.api.quote;

import java.util.Dictionary;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 7:08 PM Copyright (c) TradeHero */
public class UpdatePricesQuoteDTO
{
    public int id;
    public String yahooSymbol;
    public Dictionary<String, String> quoteData;
}