package com.tradehero.th.api.security.compact;

import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import android.support.annotation.NonNull;

public class DepositoryReceiptDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "8";

    @NonNull @Override public Integer getSecurityTypeStringResourceId()
    {
        return R.string.security_type_depository_receipt;
    }
}
