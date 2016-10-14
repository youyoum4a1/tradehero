package com.androidth.general.api.live1b;
import com.androidth.general.common.persistence.DTO;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PaymentTransferIdRequestDTO implements DTO {

    private static final String BUNDLE_KEY_AMOUNT = PaymentTransferIdRequestDTO.class.getName()+ ".amount";
    private static final String BUNDLE_KEY_CURRENCY = PaymentTransferIdRequestDTO.class.getName() + ".currency";

    @Nullable public float amount;
    @Nullable public String currency;


    public PaymentTransferIdRequestDTO()
    {
        super();
    }

    public PaymentTransferIdRequestDTO(@NonNull Bundle bundle)
    {
        this.amount = bundle.getFloat(BUNDLE_KEY_AMOUNT);
        this.currency = bundle.getString(BUNDLE_KEY_CURRENCY);
    }
}
