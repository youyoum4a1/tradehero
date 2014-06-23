package com.tradehero.th.api.security.compact;

import com.tradehero.th.api.security.SecurityCompactDTO;

public class TradableRightsIssueDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "6";

    //<editor-fold desc="Constructors">
    public TradableRightsIssueDTO()
    {
        super();
    }

    public TradableRightsIssueDTO(SecurityCompactDTO other)
    {
        super(other);
    }
    //</editor-fold>
}
