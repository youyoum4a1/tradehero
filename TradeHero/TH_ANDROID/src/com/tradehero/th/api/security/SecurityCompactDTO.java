    package com.tradehero.th.api.security;

import java.util.Date;

/** Created with IntelliJ IDEA. User: xavier Date: 9/4/13 Time: 5:30 PM To change this template use File | Settings | File Templates. */
public class SecurityCompactDTO
{
    public Integer id;
    public String symbol;
    public SecurityType securityType;
    public String name;
    public String exchange;
    public String yahooSymbol;
    public String currencyDisplay;
    public String currencyISO;
    public Double marketCap;
    public Double lastPrice;
    public String imageBlobUrl;

    private Date lastPriceDateEST;
    //// EDT/EST converted to UTC
    public Date lastPriceDateAndTimeUtc;

    public Double toUSDRate;
    public Date toUSDRateDate;

    public boolean active;

    public double askPrice;
    public double bidPrice;
    public double volume;
    public double averageDailyVolume;
    public double previousClose;
    public double open;
    public double high;
    public double low;
    public double pe;
    public double eps;

    public  Boolean marketOpen;

    public Integer pc50DMA;
    public Integer pc200DMA;
    // OK above
    public String exchangeTimezoneMsftName;
    // Example "09:30:00"
    public String exchangeOpeningTimeLocal;
    // Example "16:00:00"
    public String exchangeClosingTimeLocal;
    //
    public String secTypeDesc;

    public SecurityCompactDTO()
    {
    }
}
