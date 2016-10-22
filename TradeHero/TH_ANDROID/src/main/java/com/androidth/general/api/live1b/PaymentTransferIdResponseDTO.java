package com.androidth.general.api.live1b;

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
