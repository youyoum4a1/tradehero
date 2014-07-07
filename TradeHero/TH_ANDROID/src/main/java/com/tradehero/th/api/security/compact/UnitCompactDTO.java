package com.tradehero.th.api.security.compact;

import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import org.jetbrains.annotations.NotNull;

public class UnitCompactDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "5";

    //<editor-fold desc="Constructors">
    public UnitCompactDTO()
    {
        super();
    }

    public UnitCompactDTO(SecurityCompactDTO other)
    {
        super(other);
    }
    //</editor-fold>

    @NotNull @Override public Integer getSecurityTypeStringResourceId()
    {
        return R.string.security_type_unit;
    }
}
