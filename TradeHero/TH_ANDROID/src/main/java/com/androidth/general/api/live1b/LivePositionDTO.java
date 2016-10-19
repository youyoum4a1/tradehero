package com.androidth.general.api.live1b;

import com.androidth.general.api.portfolio.LiveOwnedPortfolioId;
import com.androidth.general.api.position.LiveOwnedPositionId;
import com.androidth.general.api.position.OwnedPositionId;
import com.androidth.general.api.position.PositionDTOKey;
import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LivePositionDTO implements DTO {

    public String OrderId;
    public int Side;
    public double Qty;
    public String Product;
    public String SecurityId;
    public double EntryPrice;
    public double UPL;
    public double StopLoss;
    public double TakeProfit;
    public int PositionResponseType;
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

    @JsonIgnore
    public PositionDTOKey getLiveOwnedPositionId()
    {
        return new LiveOwnedPositionId(0,0,0);
    }

    @JsonIgnore
    public PositionDTOKey getLivePositionDTOKey()
    {
        return getLiveOwnedPositionId();
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
                '}';
    }
}
