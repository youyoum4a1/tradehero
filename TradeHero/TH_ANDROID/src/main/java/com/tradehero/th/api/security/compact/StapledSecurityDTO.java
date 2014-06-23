package com.tradehero.th.api.security.compact;

import com.tradehero.th.api.security.SecurityCompactDTO;

public class StapledSecurityDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "11";

    //<editor-fold desc="Constructors">
    public StapledSecurityDTO()
    {
        super();
    }

    public StapledSecurityDTO(SecurityCompactDTO other)
    {
        super(other);
    }
    //</editor-fold>
}
