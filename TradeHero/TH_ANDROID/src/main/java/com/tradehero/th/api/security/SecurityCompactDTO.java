package com.tradehero.th.api.security;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.api.market.Exchange;
import com.tradehero.th.utils.SecurityUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "securityType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SecurityCompactDTO.class, name = SecurityCompactDTO.EQUITY_DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = SecurityCompactDTO.class, name = SecurityCompactDTO.FUND_DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = WarrantDTO.class, name = WarrantDTO.WARRANT_DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = SecurityCompactDTO.class, name = SecurityCompactDTO.BOND_DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = SecurityCompactDTO.class, name = SecurityCompactDTO.UNIT_DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = SecurityCompactDTO.class, name = SecurityCompactDTO.TRADABLE_RIGHTS_ISSUE_DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = SecurityCompactDTO.class, name = SecurityCompactDTO.PREFERENCE_SHARE_DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = SecurityCompactDTO.class, name = SecurityCompactDTO.DEPOSITORY_RECEIPTS_DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = SecurityCompactDTO.class, name = SecurityCompactDTO.COVERED_WARRANT_DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = SecurityCompactDTO.class, name = SecurityCompactDTO.PREFERRED_SEC_DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = SecurityCompactDTO.class, name = SecurityCompactDTO.STAPLED_SEC_DTO_DESERIALISING_TYPE),
})
public class SecurityCompactDTO extends ExtendedDTO
{
    public static final String EXCHANGE_SYMBOL_FORMAT = "%s:%s";

    public static final String EQUITY_DTO_DESERIALISING_TYPE = "1";
    public static final String FUND_DTO_DESERIALISING_TYPE = "2";
    public static final String WARRANT_DTO_DESERIALISING_TYPE = "3";
    public static final String BOND_DTO_DESERIALISING_TYPE = "4";
    public static final String UNIT_DTO_DESERIALISING_TYPE = "5";
    public static final String TRADABLE_RIGHTS_ISSUE_DTO_DESERIALISING_TYPE = "6";
    public static final String PREFERENCE_SHARE_DTO_DESERIALISING_TYPE = "7";
    public static final String DEPOSITORY_RECEIPTS_DTO_DESERIALISING_TYPE = "8";
    public static final String COVERED_WARRANT_DTO_DESERIALISING_TYPE = "9";
    public static final String PREFERRED_SEC_DTO_DESERIALISING_TYPE = "10";
    public static final String STAPLED_SEC_DTO_DESERIALISING_TYPE = "11";

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
    @Nullable public Date lastPriceDateAndTimeUtc;

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
        return getExchangeLogoId(0);
    }

    public int getExchangeLogoId(int defaultResId)
    {
        try
        {
            return Exchange.valueOf(exchange).logoId;
        }
        catch (IllegalArgumentException ex)
        {
            return defaultResId;
        }
        catch (NullPointerException ex) // there isn't any client Exchange resource with the given value exchange
        {
            Timber.e("Missing exchange resource for %s", exchange);
            return defaultResId;
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
