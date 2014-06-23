package com.tradehero.th.api.security.compact;

import com.tradehero.th.api.security.SecurityCompactDTO;

public class DepositoryReceiptDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "8";

    //<editor-fold desc="Constructors">
    public DepositoryReceiptDTO()
    {
        super();
    }

    public DepositoryReceiptDTO(SecurityCompactDTO other)
    {
        super(other);
    }
    //</editor-fold>
}
