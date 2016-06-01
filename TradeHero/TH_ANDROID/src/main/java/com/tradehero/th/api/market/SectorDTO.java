package com.ayondo.academy.api.market;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class SectorDTO extends SectorCompactDTO implements WithTopSecurities
{
    @Nullable public SecuritySuperCompactDTOList topSecurities;

    @NonNull @Override public SecuritySuperCompactDTOList getTopSecurities()
    {
        if (topSecurities != null)
        {
            return topSecurities;
        }
        return new SecuritySuperCompactDTOList();
    }
}
