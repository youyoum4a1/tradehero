package com.tradehero.th.api.quote;

import java.util.Dictionary;

public class UpdatePricesQuoteDTO
{
    public int id;
    public String yahooSymbol;
    public Dictionary<String, String> quoteData;
}