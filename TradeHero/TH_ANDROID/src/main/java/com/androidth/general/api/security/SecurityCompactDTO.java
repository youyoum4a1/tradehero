package com.androidth.general.api.security;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.R;
import com.androidth.general.api.market.Exchange;
import com.androidth.general.api.security.compact.BondCompactDTO;
import com.androidth.general.api.security.compact.CoveredWarrantDTO;
import com.androidth.general.api.security.compact.DepositoryReceiptDTO;
import com.androidth.general.api.security.compact.EquityCompactDTO;
import com.androidth.general.api.security.compact.FundCompactDTO;
import com.androidth.general.api.security.compact.FxSecurityCompactDTO;
import com.androidth.general.api.security.compact.IndexSecurityCompactDTO;
import com.androidth.general.api.security.compact.LockedSecurityCompactDTO;
import com.androidth.general.api.security.compact.PreferenceShareDTO;
import com.androidth.general.api.security.compact.PreferredSecurityDTO;
import com.androidth.general.api.security.compact.StapledSecurityDTO;
import com.androidth.general.api.security.compact.TradableRightsIssueDTO;
import com.androidth.general.api.security.compact.UnitCompactDTO;
import com.androidth.general.api.security.compact.UnitTrustSecurityCompactDTO;
import com.androidth.general.api.security.compact.WarrantDTO;
import java.util.Date;
import timber.log.Timber;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = SecurityCompactDTO.class,
        property = "securityType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LockedSecurityCompactDTO.class, name = LockedSecurityCompactDTO.DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = EquityCompactDTO.class, name = EquityCompactDTO.DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = FundCompactDTO.class, name = FundCompactDTO.DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = WarrantDTO.class, name = WarrantDTO.DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = BondCompactDTO.class, name = BondCompactDTO.DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = UnitCompactDTO.class, name = UnitCompactDTO.DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = TradableRightsIssueDTO.class, name = TradableRightsIssueDTO.DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = PreferenceShareDTO.class, name = PreferenceShareDTO.DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = DepositoryReceiptDTO.class, name = DepositoryReceiptDTO.DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = CoveredWarrantDTO.class, name = CoveredWarrantDTO.DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = PreferredSecurityDTO.class, name = PreferredSecurityDTO.DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = StapledSecurityDTO.class, name = StapledSecurityDTO.DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = IndexSecurityCompactDTO.class, name = IndexSecurityCompactDTO.DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = UnitTrustSecurityCompactDTO.class, name = UnitTrustSecurityCompactDTO.DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = FxSecurityCompactDTO.class, name = FxSecurityCompactDTO.DTO_DESERIALISING_TYPE),
})
public class SecurityCompactDTO implements DTO, Parcelable
{
    public static final String EXCHANGE_SYMBOL_FORMAT = "%s:%s";

    private final long createdAtNanoTime = System.nanoTime();
    public Integer id;
    public String symbol;
    public Integer securityType;
    public String name;
    public String country;
    public Integer exchangeId;
    public String exchange;
    public String yahooSymbol;
    public String reutersSymbol;
    public String chartDataSource;
    public String currencyDisplay;
    public String currencyISO;
    @Nullable public Double marketCap;
    @Nullable public Double lastPrice;
    public Double risePercent;
    public String imageBlobUrl;
    //// EDT/EST converted to UTC
    @Nullable public Date lastPriceDateAndTimeUtc;
    @Nullable public Double toUSDRate;
    public Date toUSDRateDate;

    private Date lastPriceDateEST;

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

    @Nullable public Boolean marketOpen;

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


    protected String timeTillNextExchangeOpen;
    protected String timeTillNextExchangeOpenSeconds;

    public String marker;
    public Boolean isCFD;
    public Double minShort;
    public Double maxShort;
    public Double minLong;
    public Double maxLong;
    public Integer sortorderInExchange;
    public Integer sortorderOverall;
    public Integer UnderlyingSecurityId;
    @Nullable public Integer lotSize;
    public Integer max_lot;
    public Integer min_lot;

    //<editor-fold desc="Constructors">
    public SecurityCompactDTO()
    {
        super();
    }

    public SecurityCompactDTO(@NonNull SecurityCompactDTO other)
    {
        super();
        this.marker = other.marker;
        this.isCFD = other.isCFD;
        this.minShort = other.minShort;
        this.maxShort = other.maxShort;
        this.minShort = other.minShort;
        this.minLong = other.minLong;
        this.maxLong = other.maxLong;
        this.sortorderInExchange = other.sortorderInExchange;
        this.sortorderOverall = other.sortorderOverall;
        this.UnderlyingSecurityId = other.UnderlyingSecurityId;
        this.lotSize = other.lotSize;

        this.id = other.id;
        this.symbol = other.symbol;
        this.securityType = other.securityType;
        this.name = other.name;
        this.country = other.country;
        this.exchangeId = other.exchangeId;
        this.exchange = other.exchange;
        this.yahooSymbol = other.yahooSymbol;
        this.reutersSymbol = other.reutersSymbol;
        this.chartDataSource = other.chartDataSource;
        this.currencyDisplay = other.currencyDisplay;
        this.currencyISO = other.currencyISO;
        this.marketCap = other.marketCap;
        this.lastPrice = other.lastPrice;
        this.risePercent = other.risePercent;
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
        this.timeTillNextExchangeOpenSeconds = other.timeTillNextExchangeOpenSeconds;
        this.timeTillNextExchangeOpen = other.timeTillNextExchangeOpen;
        this.secTypeDesc = other.secTypeDesc;
    }
    //</editor-fold>

    @Nullable public Integer getSecurityTypeStringResourceId()
    {
        return null;
    }
    @Nullable public Integer getResourceId()
    {
        return id;
    }


    @NonNull public String getExchangeSymbol()
    {
        return String.format(EXCHANGE_SYMBOL_FORMAT, exchange, symbol);
    }

    @DrawableRes public int getExchangeLogoId()
    {
        return getExchangeLogoId(R.drawable.default_image);
    }

    @DrawableRes public int getExchangeLogoId(int defaultResId)
    {
        try
        {
            return Exchange.valueOf(exchange).logoId;
        } catch (IllegalArgumentException ex)
        {
            return defaultResId;
        } catch (NullPointerException ex) // there isn't any client Exchange resource with the given value exchange
        {
            Timber.e("Missing exchange resource for %s", exchange);
            return defaultResId;
        }
    }

    @NonNull public SecurityIntegerId getSecurityIntegerId()
    {
        return new SecurityIntegerId(id);
    }

    @NonNull public SecurityId getSecurityId()
    {
        return new SecurityId(exchange, symbol, id);
    }

    @Nullable public TillExchangeOpenDuration getTillExchangeOpen()
    {
        if (TextUtils.isEmpty(timeTillNextExchangeOpen))
        {
            return null;
        }
        String timeTillNextExchangeOpen = this.timeTillNextExchangeOpen;
        int lastIndex = timeTillNextExchangeOpen.lastIndexOf(":");
        int seconds = (int) Float.parseFloat(timeTillNextExchangeOpen.substring(lastIndex + 1));
        timeTillNextExchangeOpen = timeTillNextExchangeOpen.substring(0, lastIndex);
        lastIndex = timeTillNextExchangeOpen.lastIndexOf(":");
        int minutes = Integer.parseInt(timeTillNextExchangeOpen.substring(lastIndex + 1));
        timeTillNextExchangeOpen = timeTillNextExchangeOpen.substring(0, lastIndex);
        lastIndex = timeTillNextExchangeOpen.lastIndexOf(".");
        int days;
        if (lastIndex != -1)
        {
            days = Integer.parseInt(timeTillNextExchangeOpen.substring(0, lastIndex));
        }
        else
        {
            days = 0;
        }
        int hours = Integer.parseInt(timeTillNextExchangeOpen.substring(lastIndex + 1));

        return new TillExchangeOpenDuration(createdAtNanoTime, days, hours, minutes, seconds);
    }

    @Override public String toString()
    {
        return "SecurityCompactDTO{" +
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
                '}';
    }

    public Double getVolume() {
        return volume;
    }

    public Double getRisePercent() {
        return risePercent;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.symbol);
        dest.writeValue(this.securityType);
        dest.writeString(this.name);
        dest.writeString(this.country);
        dest.writeValue(this.exchangeId);
        dest.writeString(this.exchange);
        dest.writeString(this.yahooSymbol);
        dest.writeString(this.reutersSymbol);
        dest.writeString(this.chartDataSource);
        dest.writeString(this.currencyDisplay);
        dest.writeString(this.currencyISO);
        dest.writeValue(this.marketCap);
        dest.writeValue(this.lastPrice);
        dest.writeValue(this.risePercent);
        dest.writeString(this.imageBlobUrl);
        dest.writeLong(this.lastPriceDateAndTimeUtc != null ? this.lastPriceDateAndTimeUtc.getTime() : -1);
        dest.writeValue(this.toUSDRate);
        dest.writeLong(this.toUSDRateDate != null ? this.toUSDRateDate.getTime() : -1);
        dest.writeLong(this.lastPriceDateEST != null ? this.lastPriceDateEST.getTime() : -1);
        dest.writeByte(this.active ? (byte) 1 : (byte) 0);
        dest.writeValue(this.askPrice);
        dest.writeValue(this.bidPrice);
        dest.writeValue(this.volume);
        dest.writeValue(this.averageDailyVolume);
        dest.writeValue(this.previousClose);
        dest.writeValue(this.open);
        dest.writeValue(this.high);
        dest.writeValue(this.low);
        dest.writeValue(this.pe);
        dest.writeValue(this.eps);
        dest.writeValue(this.marketOpen);
        dest.writeValue(this.pc50DMA);
        dest.writeValue(this.pc200DMA);
        dest.writeString(this.exchangeTimezoneMsftName);
        dest.writeString(this.exchangeOpeningTimeLocal);
        dest.writeString(this.exchangeClosingTimeLocal);
        dest.writeString(this.secTypeDesc);
        dest.writeString(this.timeTillNextExchangeOpen);
        dest.writeString(this.timeTillNextExchangeOpenSeconds);
        dest.writeString(this.marker);
        dest.writeValue(this.isCFD);
        dest.writeValue(this.minShort);
        dest.writeValue(this.maxShort);
        dest.writeValue(this.minLong);
        dest.writeValue(this.maxLong);
        dest.writeValue(this.sortorderInExchange);
        dest.writeValue(this.sortorderOverall);
        dest.writeValue(this.UnderlyingSecurityId);
        dest.writeValue(this.lotSize);
        dest.writeValue(this.max_lot);
        dest.writeValue(this.min_lot);
    }

    protected SecurityCompactDTO(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.symbol = in.readString();
        this.securityType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.country = in.readString();
        this.exchangeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.exchange = in.readString();
        this.yahooSymbol = in.readString();
        this.reutersSymbol = in.readString();
        this.chartDataSource = in.readString();
        this.currencyDisplay = in.readString();
        this.currencyISO = in.readString();
        this.marketCap = (Double) in.readValue(Double.class.getClassLoader());
        this.lastPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.risePercent = (Double) in.readValue(Double.class.getClassLoader());
        this.imageBlobUrl = in.readString();
        long tmpLastPriceDateAndTimeUtc = in.readLong();
        this.lastPriceDateAndTimeUtc = tmpLastPriceDateAndTimeUtc == -1 ? null : new Date(tmpLastPriceDateAndTimeUtc);
        this.toUSDRate = (Double) in.readValue(Double.class.getClassLoader());
        long tmpToUSDRateDate = in.readLong();
        this.toUSDRateDate = tmpToUSDRateDate == -1 ? null : new Date(tmpToUSDRateDate);
        long tmpLastPriceDateEST = in.readLong();
        this.lastPriceDateEST = tmpLastPriceDateEST == -1 ? null : new Date(tmpLastPriceDateEST);
        this.active = in.readByte() != 0;
        this.askPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.bidPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.volume = (Double) in.readValue(Double.class.getClassLoader());
        this.averageDailyVolume = (Double) in.readValue(Double.class.getClassLoader());
        this.previousClose = (Double) in.readValue(Double.class.getClassLoader());
        this.open = (Double) in.readValue(Double.class.getClassLoader());
        this.high = (Double) in.readValue(Double.class.getClassLoader());
        this.low = (Double) in.readValue(Double.class.getClassLoader());
        this.pe = (Double) in.readValue(Double.class.getClassLoader());
        this.eps = (Double) in.readValue(Double.class.getClassLoader());
        this.marketOpen = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.pc50DMA = (Integer) in.readValue(Integer.class.getClassLoader());
        this.pc200DMA = (Integer) in.readValue(Integer.class.getClassLoader());
        this.exchangeTimezoneMsftName = in.readString();
        this.exchangeOpeningTimeLocal = in.readString();
        this.exchangeClosingTimeLocal = in.readString();
        this.secTypeDesc = in.readString();
        this.timeTillNextExchangeOpen = in.readString();
        this.timeTillNextExchangeOpenSeconds = in.readString();
        this.marker = in.readString();
        this.isCFD = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.minShort = (Double) in.readValue(Double.class.getClassLoader());
        this.maxShort = (Double) in.readValue(Double.class.getClassLoader());
        this.minLong = (Double) in.readValue(Double.class.getClassLoader());
        this.maxLong = (Double) in.readValue(Double.class.getClassLoader());
        this.sortorderInExchange = (Integer) in.readValue(Integer.class.getClassLoader());
        this.sortorderOverall = (Integer) in.readValue(Integer.class.getClassLoader());
        this.UnderlyingSecurityId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.lotSize = (Integer) in.readValue(Integer.class.getClassLoader());
        this.max_lot = (Integer) in.readValue(Integer.class.getClassLoader());
        this.min_lot = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<SecurityCompactDTO> CREATOR = new Creator<SecurityCompactDTO>() {
        @Override
        public SecurityCompactDTO createFromParcel(Parcel source) {
            return new SecurityCompactDTO(source);
        }

        @Override
        public SecurityCompactDTO[] newArray(int size) {
            return new SecurityCompactDTO[size];
        }
    };
}
