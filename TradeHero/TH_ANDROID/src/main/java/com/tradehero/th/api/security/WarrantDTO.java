package com.tradehero.th.api.security;

import java.util.Date;

public class WarrantDTO extends SecurityCompactDTO
{
    public String warrantType;
    public Date expiryDate;
    public Double strikePrice;
    public String strikePriceCcy;

    public String issuerName;
    public String underlyingName;
    public String externalAppURL;

    public String fallbackExternalURL;

    //<editor-fold desc="Constructors">
    public WarrantDTO()
    {
        super();
    }

    public WarrantDTO(SecurityCompactDTO other)
    {
        super(other);
        this.putAll(other.getAll(), WarrantDTO.class);
    }

    public WarrantDTO(WarrantDTO other)
    {
        super(other);
        this.warrantType = other.warrantType;
        this.expiryDate = other.expiryDate;
        this.strikePrice = other.strikePrice;
        this.strikePriceCcy = other.strikePriceCcy;
        this.issuerName = other.issuerName;
        this.underlyingName = other.underlyingName;
        this.externalAppURL = other.externalAppURL;
        this.fallbackExternalURL = other.fallbackExternalURL;
        this.putAll(other.getAll(), WarrantDTO.class);
    }
    //</editor-fold>

    public boolean isMacquarieWarrant()
    {
        return this.issuerName != null &&
                (this.issuerName.toLowerCase().contains("mb") || this.issuerName.toLowerCase().contains("mbl"));
    }

    public WarrantType getWarrantType()
    {
        return WarrantType.getByShortCode(warrantType);
    }

    @Override public String toString()
    {
        return "WarrantDTO{" +
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
                //", lastPriceDateEST=" + lastPriceDateEST +
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

                ", warrantType='" + warrantType + '\'' +
                ", expiryDate=" + expiryDate +
                ", strikePrice=" + strikePrice +
                ", strikePriceCcy='" + strikePriceCcy + '\'' +
                ", issuerName='" + issuerName + '\'' +
                ", underlyingName='" + underlyingName + '\'' +
                ", externalAppURL='" + externalAppURL + '\'' +
                ", fallbackExternalURL='" + fallbackExternalURL + '\'' +
                ", extras={" + formatExtras(", ").toString() + "}" +
                '}';
    }
}
