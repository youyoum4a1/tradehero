package com.androidth.general.api.live1b;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.common.persistence.DTO;

public class LivePositionDTO implements DTO, Parcelable{

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
    @Nullable public SecurityCompactDTO SecurityCompactDto;
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

    public LivePositionDTO(SecurityCompactDTO securityCompactDto) {
        SecurityCompactDto = securityCompactDto;
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
