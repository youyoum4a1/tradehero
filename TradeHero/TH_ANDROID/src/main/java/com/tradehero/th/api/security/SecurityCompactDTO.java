    package com.tradehero.th.api.security;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.api.market.Exchange;
import com.tradehero.th.utils.SecurityUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

    /** Created with IntelliJ IDEA. User: xavier Date: 9/4/13 Time: 5:30 PM To change this template use File | Settings | File Templates. */
public class SecurityCompactDTO extends ExtendedDTO
{
    public static final String EXCHANGE_SYMBOL_FORMAT = "%s:%s";

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

    //<editor-fold desc="Constructors">
    public SecurityCompactDTO()
    {
        super();
    }

    public SecurityCompactDTO(SecurityCompactDTO other)
    {
        super();
        this.id = other.id;
        this.symbol = other.symbol;
        this.securityType = other.securityType;
        this.name = other.name;
        this.exchange = other.exchange;
        this.yahooSymbol = other.yahooSymbol;
        this.currencyDisplay = other.currencyDisplay;
        this.currencyISO = other.currencyISO;
        this.marketCap = other.marketCap;
        this.lastPrice = other.lastPrice;
        this.imageBlobUrl = other.imageBlobUrl;
        this.lastPriceDateEST = other.lastPriceDateEST;
        this.lastPriceDateAndTimeUtc = other.lastPriceDateAndTimeUtc;
        this.toUSDRate = other.toUSDRate;
        this.toUSDRateDate = other.toUSDRateDate;
        this.active = other.active;
        this.askPrice = other.askPrice;
        this.bidPrice = other.bidPrice;
        this.volume = other.volume;
        this.averageDailyVolume = other.averageDailyVolume;
        this.previousClose = other.previousClose;
        this.open = other.open;
        this.high = other.high;
        this.low = other.low;
        this.pe = other.pe;
        this.eps = other.eps;
        this.marketOpen = other.marketOpen;
        this.pc50DMA = other.pc50DMA;
        this.pc200DMA = other.pc200DMA;
        this.exchangeTimezoneMsftName = other.exchangeTimezoneMsftName;
        this.exchangeOpeningTimeLocal = other.exchangeOpeningTimeLocal;
        this.exchangeClosingTimeLocal = other.exchangeClosingTimeLocal;
        this.secTypeDesc = other.secTypeDesc;

        this.putAll(other.getAll(), SecurityCompactDTO.class);
    }
    //</editor-fold>

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
        return String.format(EXCHANGE_SYMBOL_FORMAT, exchange, symbol);
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

    public Double getLastPriceInUSD()
    {
        if (lastPrice == null || currencyISO.equalsIgnoreCase(SecurityUtils.DEFAULT_TRANSACTION_CURRENCY_ISO))
        {
            return lastPrice;
        }

        if (toUSDRate != null)
        {
            return lastPrice * toUSDRate;
        }

        return null;
    }

    @Override public String toString()
    {
        return "SecurityCompactDTO{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", securityType=" + securityType +
                ", name='" + name + '\'' +
                ", exchange='" + exchange + '\'' +
                ", yahooSymbol='" + yahooSymbol + '\'' +
                ", currencyDisplay='" + currencyDisplay + '\'' +
                ", currencyISO='" + currencyISO + '\'' +
                ", marketCap=" + marketCap +
                ", lastPrice=" + lastPrice +
                ", imageBlobUrl='" + imageBlobUrl + '\'' +
                ", lastPriceDateEST=" + lastPriceDateEST +
                ", lastPriceDateAndTimeUtc=" + lastPriceDateAndTimeUtc +
                ", toUSDRate=" + toUSDRate +
                ", toUSDRateDate=" + toUSDRateDate +
                ", active=" + active +
                ", askPrice=" + askPrice +
                ", bidPrice=" + bidPrice +
                ", volume=" + volume +
                ", averageDailyVolume=" + averageDailyVolume +
                ", previousClose=" + previousClose +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", pe=" + pe +
                ", eps=" + eps +
                ", marketOpen=" + marketOpen +
                ", pc50DMA=" + pc50DMA +
                ", pc200DMA=" + pc200DMA +
                ", exchangeTimezoneMsftName='" + exchangeTimezoneMsftName + '\'' +
                ", exchangeOpeningTimeLocal='" + exchangeOpeningTimeLocal + '\'' +
                ", exchangeClosingTimeLocal='" + exchangeClosingTimeLocal + '\'' +
                ", secTypeDesc='" + secTypeDesc + '\'' +
                ", extras={" + formatExtras(", ").toString() + "}" +
                '}';
    }
}
