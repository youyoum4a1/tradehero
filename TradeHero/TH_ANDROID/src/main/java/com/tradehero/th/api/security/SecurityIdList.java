package com.tradehero.th.api.security;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;

public class SecurityIdList extends DTOKeyIdList<SecurityId>
{
    //<editor-fold desc="Constructors">
    public SecurityIdList()
    {
        super();
    }

    public SecurityIdList(Collection<? extends SecurityId> collection)
    {
        super(collection);
    }

    public SecurityIdList(@NonNull Collection<? extends SecurityCompactDTO> compactDTOs, @Nullable SecurityCompactDTO typeQualifier)
    {
        for (SecurityCompactDTO compactDTO : compactDTOs)
        {
            add(compactDTO.getSecurityId());
        }
    }
    //</editor-fold>
}
