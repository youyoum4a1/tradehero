package com.tradehero.th.api.market;

import android.support.annotation.Nullable;

public class SectorDTO extends SectorCompactDTO implements WithTopSecurities
{
    @Nullable public SecuritySuperCompactDTOList topSecurities;

    @Nullable @Override public SecuritySuperCompactDTOList getTopSecurities()
    {
        return topSecurities;
    }
}
