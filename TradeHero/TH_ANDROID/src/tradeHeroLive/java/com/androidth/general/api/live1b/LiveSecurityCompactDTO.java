package com.androidth.general.api.live1b;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.androidth.general.R;
import com.androidth.general.api.market.Exchange;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.security.SecurityIntegerId;
import com.androidth.general.api.security.TillExchangeOpenDuration;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.utils.LiveConstants;

import java.util.Date;

import io.realm.RealmObject;
import timber.log.Timber;

public class LiveSecurityCompactDTO extends RealmObject implements DTO, Parcelable
{
    public static final String EXCHANGE_SYMBOL_FORMAT = "%s:%s";

    private long createdAtNanoTime = System.nanoTime();
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
    public String parentCurrencyISO;
    public String symbol_ay;
    public String id_ay;
    @Nullable public Double marketCap;
    @Nullable public Double lastPrice;
    public Double risePercent;
    public String imageBlobUrl;
    //// EDT/EST converted to UTC
    @Nullable public Date lastPriceDateAndTimeUtc;
    @Nullable public Double toUSDRate;
    public Date toUSDRateDate;

    public Date lastPriceDateEST;

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

    public String sector;//added

    //<editor-fold desc="Constructors">
    public LiveSecurityCompactDTO()
    {
        super();
    }

    public LiveSecurityCompactDTO(@NonNull SecurityCompactDTO other)
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

        this.id_ay = other.id_ay;
        this.symbol_ay = other.symbol_ay;
        this.parentCurrencyISO = other.parentCurrencyISO;
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

    @DrawableRes
    public int getExchangeLogoId()
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

        if(LiveConstants.isInLiveMode)
            return new SecurityId(exchange, symbol, id, Integer.parseInt(id_ay));
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

    @Override
    public String toString() {
        return "SecurityCompactDTO{" +
                "createdAtNanoTime=" + createdAtNanoTime +
                ", id=" + id +
                ", symbol='" + symbol + '\'' +
                ", securityType=" + securityType +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", exchangeId=" + exchangeId +
                ", exchange='" + exchange + '\'' +
                ", yahooSymbol='" + yahooSymbol + '\'' +
                ", reutersSymbol='" + reutersSymbol + '\'' +
                ", chartDataSource='" + chartDataSource + '\'' +
                ", currencyDisplay='" + currencyDisplay + '\'' +
                ", currencyISO='" + currencyISO + '\'' +
                ", parentCurrencyISO='" + parentCurrencyISO  + '\'' +
                ", symbol_ay='" + symbol_ay + '\'' +
                ", id_ay='" + id_ay + '\'' +
                ", marketCap=" + marketCap +
                ", lastPrice=" + lastPrice +
                ", risePercent=" + risePercent +
                ", imageBlobUrl='" + imageBlobUrl + '\'' +
                ", lastPriceDateAndTimeUtc=" + lastPriceDateAndTimeUtc +
                ", toUSDRate=" + toUSDRate +
                ", toUSDRateDate=" + toUSDRateDate +
                ", lastPriceDateEST=" + lastPriceDateEST +
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
                ", timeTillNextExchangeOpen='" + timeTillNextExchangeOpen + '\'' +
                ", timeTillNextExchangeOpenSeconds='" + timeTillNextExchangeOpenSeconds + '\'' +
                ", marker='" + marker + '\'' +
                ", isCFD=" + isCFD +
                ", minShort=" + minShort +
                ", maxShort=" + maxShort +
                ", minLong=" + minLong +
                ", maxLong=" + maxLong +
                ", sortorderInExchange=" + sortorderInExchange +
                ", sortorderOverall=" + sortorderOverall +
                ", UnderlyingSecurityId=" + UnderlyingSecurityId +
                ", lotSize=" + lotSize +
                ", max_lot=" + max_lot +
                ", min_lot=" + min_lot +
                '}';
    }

    public Double getVolume() {
        return volume;
    }

    public Double getRisePercent() {
        return risePercent;
    }


    /**
     * Parcelable implementations
     * @param other
     */
    private LiveSecurityCompactDTO(Parcel other){
        this.marker = other.readString();
        this.isCFD = other.readByte() == 1;
        this.minShort = other.readDouble();
        this.maxShort = other.readDouble();
        this.minShort = other.readDouble();
        this.minLong = other.readDouble();
        this.maxLong = other.readDouble();
        this.sortorderInExchange = other.readInt();
        this.sortorderOverall = other.readInt();
        this.UnderlyingSecurityId = other.readInt();
        this.lotSize = other.readInt();


        this.id = other.readInt();
        this.symbol = other.readString();
        this.name = other.readString();
        this.exchange = other.readString();
        this.yahooSymbol = other.readString();
        this.currencyDisplay = other.readString();
        this.currencyISO = other.readString();
        this.marketCap = other.readDouble();
        this.lastPrice = other.readDouble();
        this.imageBlobUrl = other.readString();
        this.lastPriceDateEST = new Date(other.readLong());
        this.lastPriceDateAndTimeUtc = new Date(other.readLong());
        this.toUSDRate = other.readDouble();
        this.toUSDRateDate = new Date(other.readLong());
        this.active = other.readByte() == 1;
        this.askPrice = other.readDouble();
        this.bidPrice = other.readDouble();
        this.volume = other.readDouble();
        this.averageDailyVolume = other.readDouble();
        this.previousClose = other.readDouble();
        this.open = other.readDouble();
        this.high = other.readDouble();
        this.low = other.readDouble();
        this.pe = other.readDouble();
        this.eps = other.readDouble();
        this.marketOpen = other.readByte() == 1;
        this.pc50DMA = other.readInt();
        this.pc200DMA = other.readInt();
        this.exchangeTimezoneMsftName = other.readString();
        this.exchangeOpeningTimeLocal = other.readString();
        this.exchangeClosingTimeLocal = other.readString();
        this.secTypeDesc = other.readString();
        this.risePercent = other.readDouble();
        this.timeTillNextExchangeOpen = other.readString();
    }

    public static final Parcelable.Creator<LiveSecurityCompactDTO> CREATOR = new Parcelable.Creator<LiveSecurityCompactDTO>(){
        @Override
        public LiveSecurityCompactDTO createFromParcel(Parcel source) {
            return new LiveSecurityCompactDTO(source);
        }

        @Override
        public LiveSecurityCompactDTO[] newArray(int size) {
            return new LiveSecurityCompactDTO[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        try{
            dest.writeString(this.marker);
            dest.writeByte((byte) (this.isCFD ? 1 : 0));
            dest.writeDouble(this.minShort);
            dest.writeDouble(this.maxShort);
            dest.writeDouble(this.minLong);
            dest.writeDouble(this.maxLong);
            dest.writeInt(this.sortorderInExchange);
            dest.writeInt(this.sortorderOverall);
            dest.writeInt(this.UnderlyingSecurityId);
            dest.writeInt(this.lotSize);

            dest.writeInt(this.id);
            dest.writeString(this.symbol);
            dest.writeString(this.name);
            dest.writeString(this.exchange);
            dest.writeString(this.yahooSymbol);
            dest.writeString(this.currencyDisplay);
            dest.writeString(this.currencyISO);
            dest.writeDouble(this.marketCap);
            dest.writeDouble(this.lastPrice);
            dest.writeString(this.imageBlobUrl);
            dest.writeLong(this.lastPriceDateEST.getTime());
            dest.writeLong(this.lastPriceDateAndTimeUtc.getTime());
            dest.writeDouble(this.toUSDRate);
            dest.writeLong(this.toUSDRateDate.getTime());
            dest.writeByte((byte) (this.active ? 1 : 0));
            dest.writeDouble(this.askPrice);
            dest.writeDouble(this.bidPrice);
            dest.writeDouble(this.volume);
            dest.writeDouble(this.averageDailyVolume);
            dest.writeDouble(this.previousClose);
            dest.writeDouble(this.open);
            dest.writeDouble(this.high);
            dest.writeDouble(this.low);
            dest.writeDouble(this.pe);
            dest.writeDouble(this.eps);
            dest.writeByte((byte) (this.marketOpen ? 1 : 0));
            dest.writeInt(this.pc50DMA);
            dest.writeInt(this.pc200DMA);
            dest.writeString(this.exchangeTimezoneMsftName);
            dest.writeString(this.exchangeOpeningTimeLocal);
            dest.writeString(this.exchangeClosingTimeLocal);
            dest.writeString(this.secTypeDesc);
            dest.writeDouble(this.risePercent);
            dest.writeString(this.timeTillNextExchangeOpen);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
