    package com.tradehero.th.api.security;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.market.Exchange;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

    /** Created with IntelliJ IDEA. User: xavier Date: 9/4/13 Time: 5:30 PM To change this template use File | Settings | File Templates. */
public class SecurityCompactDTO implements DTO
{
    public static final String EXCHANGE_SYMBOL_FOMAT = "%s:%s";

    public Integer id;
    public String symbol;
    public int securityType;
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

    public Double askPrice;
    public Double bidPrice;
    public Double volume;
    public Double averageDailyVolume;
    public Double previousClose;
    public Double open;
    public Double high;
    public Double low;
    public Double pe;
    public Double eps;

    public Boolean marketOpen;

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
        super();
    }

    public SecurityCompactDTO(SecurityCompactDTO other)
    {
        this.active = other.active;
        this.askPrice = other.askPrice;
        this.averageDailyVolume = other.averageDailyVolume;
        this.bidPrice = other.bidPrice;
        this.currencyDisplay = other.currencyDisplay;
        this.currencyISO = other.currencyISO;
        this.eps = other.eps;
        this.exchange = other.exchange;
        this.exchangeClosingTimeLocal = other.exchangeClosingTimeLocal;
        this.exchangeOpeningTimeLocal = other.exchangeOpeningTimeLocal;
        this.exchangeTimezoneMsftName = other.exchangeTimezoneMsftName;
        this.high = other.high;
        this.id = other.id;
        this.imageBlobUrl = other.imageBlobUrl;
        this.lastPrice = other.lastPrice;
        this.lastPriceDateAndTimeUtc = other.lastPriceDateAndTimeUtc;
        this.lastPriceDateEST = other.lastPriceDateEST;
        this.low = other.low;
        this.marketCap = other.marketCap;
        this.marketOpen = other.marketOpen;
        this.name = other.name;
        this.open = other.open;
        this.pc200DMA = other.pc200DMA;
        this.pc50DMA = other.pc50DMA;
        this.pe = other.pe;
        this.previousClose = other.previousClose;
        this.secTypeDesc = other.secTypeDesc;
        this.securityType = other.securityType;
        this.symbol = other.symbol;
        this.toUSDRate = other.toUSDRate;
        this.toUSDRateDate = other.toUSDRateDate;
        this.volume = other.volume;
        this.yahooSymbol = other.yahooSymbol;
    }

    public SecurityType getSecurityType()
    {
        return SecurityType.getByValue(securityType);
    }

    public int getSecurityTypeStringResourceId()
    {
        return getSecurityType().stringResId;
    }

    public String getExchangeSymbol()
    {
        return String.format(EXCHANGE_SYMBOL_FOMAT, exchange, symbol);
    }

    public int getExchangeLogoId()
    {
        try
        {
            return Exchange.valueOf(exchange).logoId;
        }
        catch (IllegalArgumentException ex)
        {
            return 0;
        }
    }

    public boolean isLastPriceNotNullOrZero()
    {
        return !Double.isNaN(lastPrice) && !(Double.compare(lastPrice, 0.0) == 0);
    }

    public SecurityIntegerId getSecurityIntegerId()
    {
        return new SecurityIntegerId(id);
    }

    public SecurityId getSecurityId()
    {
        return new SecurityId(exchange, symbol);
    }

    public static List<SecurityId> getSecurityIds(List<SecurityCompactDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        List<SecurityId> securityIds = new ArrayList<>();
        for (SecurityCompactDTO value: values)
        {
            securityIds.add(value.getSecurityId());
        }
        return securityIds;
    }
}
