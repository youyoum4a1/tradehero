package com.tradehero.th.api.security.compact;

import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import android.support.annotation.NonNull;

public class CoveredWarrantDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "9";

    @NonNull @Override public Integer getSecurityTypeStringResourceId()
    {
        return R.string.security_type_covered_warrant;
    }
}
