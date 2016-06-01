package com.ayondo.academy.api.security.compact;

import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.api.security.SecurityCompactDTO;

public class TradableRightsIssueDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "6";

    @NonNull @Override public Integer getSecurityTypeStringResourceId()
    {
        return R.string.security_type_tradable_rights_issue;
    }
}
