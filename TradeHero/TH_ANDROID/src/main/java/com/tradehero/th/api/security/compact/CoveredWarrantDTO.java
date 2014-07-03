package com.tradehero.th.api.security.compact;

import com.tradehero.th.api.security.SecurityCompactDTO;

public class CoveredWarrantDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "9";

    //<editor-fold desc="Constructors">
    public CoveredWarrantDTO()
    {
        super();
    }

    public CoveredWarrantDTO(SecurityCompactDTO other)
    {
        super(other);
    }
    //</editor-fold>
}
