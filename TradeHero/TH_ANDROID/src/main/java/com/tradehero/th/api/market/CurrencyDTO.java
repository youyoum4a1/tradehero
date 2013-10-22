package com.tradehero.th.api.market;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 9:18 PM To change this template use File | Settings | File Templates. */
public class CurrencyDTO
{
    public static final String TAG = CurrencyDTO.class.getSimpleName();

    public int id;
    public String symbol;
    public String thDisplayString;
    public double toUsdRate;
    public String toUsdAsOf;

    public CurrencyDTO()
    {
        super();
    }
}
