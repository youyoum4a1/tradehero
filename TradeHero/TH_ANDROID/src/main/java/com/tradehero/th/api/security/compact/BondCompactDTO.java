package com.tradehero.th.api.security.compact;

import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import org.jetbrains.annotations.NotNull;

public class BondCompactDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "4";

    @NotNull @Override public Integer getSecurityTypeStringResourceId()
    {
        return R.string.security_type_bond;
    }
}
