package com.tradehero.th.api.security.compact;

import com.tradehero.th.api.security.SecurityCompactDTO;

public class EquityCompactDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "1";

    //<editor-fold desc="Constructors">
    public EquityCompactDTO()
    {
        super();
    }

    public EquityCompactDTO(SecurityCompactDTO other)
    {
        super(other);
    }
    //</editor-fold>
}
