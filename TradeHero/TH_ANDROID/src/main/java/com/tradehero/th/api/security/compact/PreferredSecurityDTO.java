package com.tradehero.th.api.security.compact;

import com.tradehero.th.api.security.SecurityCompactDTO;

public class PreferredSecurityDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "10";

    //<editor-fold desc="Constructors">
    public PreferredSecurityDTO()
    {
        super();
    }

    public PreferredSecurityDTO(SecurityCompactDTO other)
    {
        super(other);
    }
    //</editor-fold>
}
