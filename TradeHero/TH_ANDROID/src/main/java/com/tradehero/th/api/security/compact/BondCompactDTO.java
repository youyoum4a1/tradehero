package com.tradehero.th.api.security.compact;

import com.tradehero.th.api.security.SecurityCompactDTO;

public class BondCompactDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "4";

    //<editor-fold desc="Constructors">
    public BondCompactDTO()
    {
        super();
    }

    public BondCompactDTO(SecurityCompactDTO other)
    {
        super(other);
    }
    //</editor-fold>
}
