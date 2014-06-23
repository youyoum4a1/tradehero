package com.tradehero.th.api.security.compact;

import com.tradehero.th.api.security.SecurityCompactDTO;

public class LockedSecurityCompactDTO extends SecurityCompactDTO
{
    // HACK server did it...
    public static final String DTO_DESERIALISING_TYPE = "0";

    //<editor-fold desc="Constructors">
    public LockedSecurityCompactDTO()
    {
        super();
    }

    public LockedSecurityCompactDTO(SecurityCompactDTO other)
    {
        super(other);
    }
    //</editor-fold>
}
