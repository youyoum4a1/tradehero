package com.tradehero.th.api.market;


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
