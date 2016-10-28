package com.androidth.general.api.live1b;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.common.persistence.DTO;

import io.realm.RealmObject;

public class LivePositionDTO extends RealmObject implements DTO, Parcelable{

    public String OrderId;
    public Integer Side;
    public Double Qty;
    public String Product;
    public String SecurityId;
    public Double EntryPrice;
    public Double UPL;
    public Double StopLoss;
    public Double TakeProfit;
    public Integer PositionResponseType;
    @Nullable public LiveSecurityCompactDTO SecurityCompactDto;
//    public PositionResponseTypeEnum PositionResponseType;
//    @JsonProperty("Side")
//    public OrderSideEnum side;
//    @JsonProperty("Qty")
//    public float quantity;
//    @JsonProperty("Product")
//    public String product;
//    @JsonProperty("SecurityId")
//    public String securityIdLive;
//    @JsonProperty("EntryPrice")
//    public float entryPrice;
//    @JsonProperty("UPL")
//    public float UPL;
//    @JsonProperty("StopLoss")
//    public float stopLoss;
//    @JsonProperty("TakeProfit")
//    public float takeProfit;
//    @JsonProperty("PositionResponseType")
//    public PositionResponseTypeEnum positionResponseTypeEnum;

    //<editor-fold desc="Constructors">

    public LivePositionDTO()
    {
    }

    public LivePositionDTO(SecurityCompactDTO other) {
//        SecurityCompactDto =  securityCompactDto;
        if(this.SecurityCompactDto==null)
            this.SecurityCompactDto = new LiveSecurityCompactDTO();

        this.SecurityCompactDto.marker = other.marker;
        this.SecurityCompactDto.isCFD = other.isCFD;
        this.SecurityCompactDto.minShort = other.minShort;
        this.SecurityCompactDto.maxShort = other.maxShort;
        this.SecurityCompactDto.minShort = other.minShort;
        this.SecurityCompactDto.minLong = other.minLong;
        this.SecurityCompactDto.maxLong = other.maxLong;
        this.SecurityCompactDto.sortorderInExchange = other.sortorderInExchange;
        this.SecurityCompactDto.sortorderOverall = other.sortorderOverall;
        this.SecurityCompactDto.UnderlyingSecurityId = other.UnderlyingSecurityId;
        this.SecurityCompactDto.lotSize = other.lotSize;

        this.SecurityCompactDto.id = other.id;
        this.SecurityCompactDto.symbol = other.symbol;
        this.SecurityCompactDto.securityType = other.securityType;
        this.SecurityCompactDto.name = other.name;
        this.SecurityCompactDto.country = other.country;
        this.SecurityCompactDto.exchangeId = other.exchangeId;
        this.SecurityCompactDto.exchange = other.exchange;
        this.SecurityCompactDto.yahooSymbol = other.yahooSymbol;
        this.SecurityCompactDto.reutersSymbol = other.reutersSymbol;
        this.SecurityCompactDto.chartDataSource = other.chartDataSource;
        this.SecurityCompactDto.currencyDisplay = other.currencyDisplay;
        this.SecurityCompactDto.currencyISO = other.currencyISO;
        this.SecurityCompactDto.marketCap = other.marketCap;
        this.SecurityCompactDto.lastPrice = other.lastPrice;
        this.SecurityCompactDto.risePercent = other.risePercent;
        this.SecurityCompactDto.imageBlobUrl = other.imageBlobUrl;
        this.SecurityCompactDto.lastPriceDateEST = other.lastPriceDateEST;
        this.SecurityCompactDto.lastPriceDateAndTimeUtc = other.lastPriceDateAndTimeUtc;
        this.SecurityCompactDto.toUSDRate = other.toUSDRate;
        this.SecurityCompactDto.toUSDRateDate = other.toUSDRateDate;
        this.SecurityCompactDto.active = other.active;
        this.SecurityCompactDto.askPrice = other.askPrice;
        this.SecurityCompactDto.bidPrice = other.bidPrice;
        this.SecurityCompactDto.volume = other.volume;
        this.SecurityCompactDto.averageDailyVolume = other.averageDailyVolume;
        this.SecurityCompactDto.previousClose = other.previousClose;
        this.SecurityCompactDto.open = other.open;
        this.SecurityCompactDto.high = other.high;
        this.SecurityCompactDto.low = other.low;
        this.SecurityCompactDto.pe = other.pe;
        this.SecurityCompactDto.eps = other.eps;
        this.SecurityCompactDto.marketOpen = other.marketOpen;
        this.SecurityCompactDto.pc50DMA = other.pc50DMA;
        this.SecurityCompactDto.pc200DMA = other.pc200DMA;
        this.SecurityCompactDto.exchangeTimezoneMsftName = other.exchangeTimezoneMsftName;
        this.SecurityCompactDto.exchangeOpeningTimeLocal = other.exchangeOpeningTimeLocal;
        this.SecurityCompactDto.exchangeClosingTimeLocal = other.exchangeClosingTimeLocal;
        this.SecurityCompactDto.timeTillNextExchangeOpenSeconds = other.timeTillNextExchangeOpenSeconds;
        this.SecurityCompactDto.timeTillNextExchangeOpen = other.timeTillNextExchangeOpen;
        this.SecurityCompactDto.secTypeDesc = other.secTypeDesc;

        this.SecurityCompactDto.id_ay = other.id_ay;
        this.SecurityCompactDto.symbol_ay = other.symbol_ay;
        this.SecurityCompactDto.parentCurrencyISO = other.parentCurrencyISO;

    }

    @Override
    public String toString() {
        return "LivePositionDTO{" +
                "OrderId='" + OrderId + '\'' +
                ", Side=" + Side +
                ", Qty=" + Qty +
                ", Product='" + Product + '\'' +
                ", SecurityId='" + SecurityId + '\'' +
                ", EntryPrice=" + EntryPrice +
                ", UPL=" + UPL +
                ", StopLoss=" + StopLoss +
                ", TakeProfit=" + TakeProfit +
                ", PositionResponseType=" + PositionResponseType +
                ", SecurityCompactDto=" + SecurityCompactDto +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    //Parcelable implementations
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.OrderId);
        dest.writeValue(this.Side);
        dest.writeValue(this.Qty);
        dest.writeString(this.Product);
        dest.writeString(this.SecurityId);
        dest.writeValue(this.EntryPrice);
        dest.writeValue(this.UPL);
        dest.writeValue(this.StopLoss);
        dest.writeValue(this.TakeProfit);
        dest.writeValue(this.PositionResponseType);
        dest.writeParcelable(this.SecurityCompactDto, flags);
    }

    protected LivePositionDTO(Parcel in) {
        this.OrderId = in.readString();
        this.Side = (Integer) in.readValue(Integer.class.getClassLoader());
        this.Qty = (Double) in.readValue(Double.class.getClassLoader());
        this.Product = in.readString();
        this.SecurityId = in.readString();
        this.EntryPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.UPL = (Double) in.readValue(Double.class.getClassLoader());
        this.StopLoss = (Double) in.readValue(Double.class.getClassLoader());
        this.TakeProfit = (Double) in.readValue(Double.class.getClassLoader());
        this.PositionResponseType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.SecurityCompactDto = in.readParcelable(SecurityCompactDTO.class.getClassLoader());
    }

    public static final Creator<LivePositionDTO> CREATOR = new Creator<LivePositionDTO>() {
        @Override
        public LivePositionDTO createFromParcel(Parcel source) {
            return new LivePositionDTO(source);
        }

        @Override
        public LivePositionDTO[] newArray(int size) {
            return new LivePositionDTO[size];
        }
    };
}
