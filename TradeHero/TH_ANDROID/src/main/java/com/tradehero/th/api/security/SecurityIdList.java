package com.tradehero.th.api.security;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public SecurityIdList(@NotNull Collection<? extends SecurityCompactDTO> compactDTOs, @Nullable SecurityCompactDTO typeQualifier)
    {
        for (SecurityCompactDTO compactDTO : compactDTOs)
        {
            add(compactDTO.getSecurityId());
        }
    }
    //</editor-fold>
}
