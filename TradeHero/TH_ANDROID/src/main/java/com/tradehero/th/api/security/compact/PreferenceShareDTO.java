package com.tradehero.th.api.security.compact;

import com.tradehero.th.api.security.SecurityCompactDTO;

public class PreferenceShareDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "7";

    //<editor-fold desc="Constructors">
    public PreferenceShareDTO()
    {
        super();
    }

    public PreferenceShareDTO(SecurityCompactDTO other)
    {
        super(other);
    }
    //</editor-fold>
}
