package com.androidth.general.api.live1b;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NewOrderSingleDTO implements DTO
{
    private static final String BUNDLE_KEY_SECURITY_ID = NewOrderSingleDTO.class.getName() + ".security_id";
    private static final String BUNDLE_KEY_ORDER_SIDE = NewOrderSingleDTO.class.getName()+ ".order_side";
    private static final String BUNDLE_KEY_QUANTITY = NewOrderSingleDTO.class.getName() + ".quantity";

    @JsonProperty("SecurityID")
    @Nullable public String securityID;
    @JsonProperty("Side")
    @Nullable public OrderSideEnum orderSide;
    @JsonProperty("Qty")
    @Nullable public float quantity;


    public NewOrderSingleDTO()
    {
        super();
    }

    public NewOrderSingleDTO(@NonNull Bundle bundle)
    {
        securityID = bundle.getString(BUNDLE_KEY_SECURITY_ID);
        orderSide = OrderSideEnum.getOrderSideEnumFromAsciiId(bundle.getInt(BUNDLE_KEY_ORDER_SIDE));
        quantity = bundle.getFloat(BUNDLE_KEY_QUANTITY);

    }

    public NewOrderSingleDTO(String securityID, OrderSideEnum orderSide, float quantity)
    {
        this.securityID = securityID;
        this.orderSide = orderSide;
        this.quantity = quantity;
    }
}
