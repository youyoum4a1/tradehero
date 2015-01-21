package com.tradehero.th.api.security.compact;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.WarrantType;
import com.tradehero.th.api.security.WarrantTypeShortCodeDef;
import java.util.Date;

public class WarrantDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "3";
    @WarrantTypeShortCodeDef public static final String CALL_SHORT_CODE = "C";
    @WarrantTypeShortCodeDef public static final String PUT_SHORT_CODE = "P";

    @WarrantTypeShortCodeDef public String warrantType;
    public Date expiryDate;
    public Double strikePrice;
    public String strikePriceCcy;

    public String issuerName;
    public String underlyingName;
    public String externalAppURL;

    public String fallbackExternalURL;

    @NonNull @Override public Integer getSecurityTypeStringResourceId()
    {
        return R.string.security_type_warrant;
    }

    @Nullable public WarrantType getWarrantType()
    {
        return WarrantType.getByShortCode(warrantType);
    }

    @Override public String toString()
    {
        return "WarrantDTO{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
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
                '}';
    }
}
