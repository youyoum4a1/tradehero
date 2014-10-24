package com.tradehero.th.api.quote;

import java.io.Serializable;
import java.util.Dictionary;

public class UpdatePricesQuoteDTO implements Serializable
{
    public int id;
    public String yahooSymbol;
    public Dictionary<String, String> quoteData;
}