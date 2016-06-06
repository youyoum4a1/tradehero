package com.androidth.general.api.security.compact;

import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.api.security.SecurityCompactDTO;

public class EquityCompactDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "1";

    @NonNull @Override public Integer getSecurityTypeStringResourceId()
    {
        return R.string.security_type_equity;
    }
}
