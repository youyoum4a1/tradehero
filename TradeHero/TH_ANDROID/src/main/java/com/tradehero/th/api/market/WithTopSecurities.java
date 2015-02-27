package com.tradehero.th.api.market;

import android.support.annotation.NonNull;

public interface WithTopSecurities
{
    @NonNull SecuritySuperCompactDTOList getTopSecurities();
}
