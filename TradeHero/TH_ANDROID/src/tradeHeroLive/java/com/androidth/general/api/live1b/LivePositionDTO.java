package com.androidth.general.api.live1b;

import android.support.annotation.Nullable;

import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.common.persistence.DTO;

public class LivePositionDTO implements DTO {

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
    //</editor-fold>

//    @JsonIgnore
//    public PositionDTOKey getLiveOwnedPositionId()
//    {
//        return new LiveOwnedPositionId(0,0,0);
//    }
//
//    @JsonIgnore
//    public PositionDTOKey getLivePositionDTOKey()
//    {
//        return getLiveOwnedPositionId();
//    }

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
                '}';
    }

    public class LiveSecurityCompactDTO {
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
    }
}
