package com.androidth.general.api.live1b;

import android.os.Bundle;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.common.persistence.DTO;

public class PaymentTransferIdResponseDTO extends BaseMessageResponseDTO implements DTO
{

    public String transferId;

    public PaymentTransferIdResponseDTO()
    {
        super();
    }

    public PaymentTransferIdResponseDTO(String transferId)
    {
        this.transferId = transferId;
    }
}
