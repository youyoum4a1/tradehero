package com.androidth.general.api.live1b;

import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PositionDTO implements DTO {

    @JsonProperty("OrderId")
    public String orderId;
    @JsonProperty("Side")
    public OrderSideEnum side;
    @JsonProperty("Qty")
    public float quantity;
    @JsonProperty("Product")
    public String product;
    @JsonProperty("SecurityId")
    public String securityIdLive;
    @JsonProperty("EntryPrice")
    public float entryPrice;
    @JsonProperty("UPL")
    public float UPL;
    @JsonProperty("StopLoss")
    public float stopLoss;
    @JsonProperty("TakeProfit")
    public float takeProfit;
    @JsonProperty("PositionResponseType")
    public PositionResponseTypeEnum positionResponseTypeEnum;

    //<editor-fold desc="Constructors">
    public PositionDTO()
    {
    }
    //</editor-fold>


    public PositionDTO(String orderId, OrderSideEnum side, float quantity, String product, String securityIdLive, float entryPrice, float UPL, float stopLoss, float takeProfit, PositionResponseTypeEnum positionResponseTypeEnum) {
        this.orderId = orderId;
        this.side = side;
        this.quantity = quantity;
        this.product = product;
        this.securityIdLive = securityIdLive;
        this.entryPrice = entryPrice;
        this.UPL = UPL;
        this.stopLoss = stopLoss;
        this.takeProfit = takeProfit;
        this.positionResponseTypeEnum = positionResponseTypeEnum;
    }

    @Override
    public String toString() {
        return "PositionDTO{" +
                "orderId='" + orderId + '\'' +
                ", side=" + side +
                ", quantity=" + quantity +
                ", product='" + product + '\'' +
                ", securityIdLive='" + securityIdLive + '\'' +
                ", entryPrice=" + entryPrice +
                ", UPL=" + UPL +
                ", stopLoss=" + stopLoss +
                ", takeProfit=" + takeProfit +
                ", positionResponseTypeEnum=" + positionResponseTypeEnum +
                '}';
    }
}
