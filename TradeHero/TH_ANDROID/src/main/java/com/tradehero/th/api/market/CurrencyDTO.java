package com.ayondo.academy.api.market;

public class CurrencyDTO
{
    public int id;
    public String symbol;
    public String thDisplayString;
    public double toUsdRate;
    public String toUsdAsOf;

    //<editor-fold desc="Constructors">
    public CurrencyDTO()
    {
        super();
    }
    //</editor-fold>
}
