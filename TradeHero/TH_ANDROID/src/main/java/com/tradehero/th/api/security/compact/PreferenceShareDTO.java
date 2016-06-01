package com.ayondo.academy.api.security.compact;

import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.api.security.SecurityCompactDTO;

public class PreferenceShareDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "7";

    @NonNull @Override public Integer getSecurityTypeStringResourceId()
    {
        return R.string.security_type_preference_share;
    }
}
