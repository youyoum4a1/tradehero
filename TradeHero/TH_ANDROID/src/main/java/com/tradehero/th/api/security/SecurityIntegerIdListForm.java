package com.tradehero.th.api.security;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SecurityIntegerIdListForm
{
    @NonNull public List<Integer> securityIds;

    //<editor-fold desc="Constructors">
    public SecurityIntegerIdListForm()
    {
        super();
        securityIds = new ArrayList<>();
    }

    public SecurityIntegerIdListForm(
            @NonNull Collection<? extends SecurityIntegerId> securityIntegerIds,
            @SuppressWarnings("UnusedParameters") @Nullable SecurityIntegerId typeQualifier)
    {
        this();
        for (SecurityIntegerId securityIntegerId : securityIntegerIds)
        {
            securityIds.add(securityIntegerId.key);
        }
    }

    public SecurityIntegerIdListForm(
            @NonNull Collection<? extends SecurityCompactDTO> securityCompactDTOs,
            @SuppressWarnings("UnusedParameters") @Nullable SecurityCompactDTO typeQualifier)
    {
        this();
        for (SecurityCompactDTO  securityCompactDTO : securityCompactDTOs)
        {
            securityIds.add(securityCompactDTO.id);
        }
    }
    //</editor-fold>
}
