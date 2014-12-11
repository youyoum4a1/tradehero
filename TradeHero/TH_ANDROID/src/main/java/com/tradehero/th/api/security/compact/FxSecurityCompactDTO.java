package com.tradehero.th.api.security.compact;

import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.key.FxPairSecurityId;

public class FxSecurityCompactDTO extends SecurityCompactDTO
{
    public static final String DTO_DESERIALISING_TYPE = "14";

    @NonNull @Override public Integer getSecurityTypeStringResourceId()
    {
        return R.string.security_type_fx;
    }

    @NonNull public FxPairSecurityId getFxPair()
    {
        String[] split = symbol.split("_");
        return new FxPairSecurityId(split[0], split[1]);
    }
}
