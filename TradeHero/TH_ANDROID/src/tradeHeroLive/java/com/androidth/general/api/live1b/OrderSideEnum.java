package com.androidth.general.api.live1b;


import android.support.annotation.NonNull;

public enum OrderSideEnum {
    BUY ('1'),
    SELL ('2'),
    BUY_MINUS ('3'),
    SELL_PLUS ('4'),
    SELL_SHORT ('5'),
    SELL_SHORT_EXEMPT ('6'),
    UNDISCLOSED ('7'),
    CROSS ('8'),
    CROSS_SHORT ('9'),
    CROSS_SHORT_EXEMPT ('A'),
    AS_DEFINED ('B'),
    OPPOSITE ('C'),
    SUBSCRIBE ('D'),
    REDEEM ('E'),
    LEND ('F'),
    BORROW ('G');

    private final char orderSideId;
    public final int asciiSideId;

    OrderSideEnum(@NonNull char orderSideId)
    {

        this.orderSideId = orderSideId;
        this.asciiSideId = (int)orderSideId;
    }

    public char getOrderSideId() {
        return orderSideId;
    }

    @Override
    public String toString() {
        return this.toString() + " " + getOrderSideId() + " " + this.asciiSideId;
    }

    public static OrderSideEnum getOrderSideEnumFromId(char id) {
        if ((int)id!=0)
            for(OrderSideEnum b : OrderSideEnum.values())
                if(id==b.getOrderSideId())
                    return b;

        return null;
    }

    public static OrderSideEnum getOrderSideEnumFromAsciiId(int id) {
        if (id!=0)
            for(OrderSideEnum b : OrderSideEnum.values())
                if(id==b.asciiSideId)
                    return b;

        return null;
    }
}
