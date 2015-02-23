package com.tradehero.th.api.market;

import android.support.annotation.Nullable;

public interface WithTopSecurities
{
    @Nullable SecuritySuperCompactDTOList getTopSecurities();
}
