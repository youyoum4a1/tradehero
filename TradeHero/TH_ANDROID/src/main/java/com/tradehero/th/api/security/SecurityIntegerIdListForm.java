package com.tradehero.th.api.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SecurityIntegerIdListForm
{
    @NotNull public List<Integer> securityIds;

    //<editor-fold desc="Constructors">
    public SecurityIntegerIdListForm()
    {
        super();
        securityIds = new ArrayList<>();
    }

    public SecurityIntegerIdListForm(
            @NotNull Collection<? extends SecurityIntegerId> securityIntegerIds,
            @SuppressWarnings("UnusedParameters") @Nullable SecurityIntegerId typeQualifier)
    {
        this();
        for (@NotNull SecurityIntegerId securityIntegerId : securityIntegerIds)
        {
            securityIds.add(securityIntegerId.key);
        }
    }

    public SecurityIntegerIdListForm(
            @NotNull Collection<? extends SecurityCompactDTO> securityCompactDTOs,
            @SuppressWarnings("UnusedParameters") @Nullable SecurityCompactDTO typeQualifier)
    {
        this();
        for (@NotNull SecurityCompactDTO  securityCompactDTO : securityCompactDTOs)
        {
            securityIds.add(securityCompactDTO.id);
        }
    }
    //</editor-fold>
}
