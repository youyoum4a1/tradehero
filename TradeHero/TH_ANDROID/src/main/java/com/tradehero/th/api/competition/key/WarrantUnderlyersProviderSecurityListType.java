package com.ayondo.academy.api.competition.key;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.api.security.key.SecurityListType;

public class WarrantUnderlyersProviderSecurityListType extends ProviderSecurityListType
{
    //<editor-fold desc="Constructors">
    public WarrantUnderlyersProviderSecurityListType(ProviderSecurityListType other)
    {
        super(other);
    }

    public WarrantUnderlyersProviderSecurityListType(ProviderId providerId, Integer page, Integer perPage)
    {
        super(providerId, page, perPage);
    }

    public WarrantUnderlyersProviderSecurityListType(ProviderId providerId, Integer page)
    {
        super(providerId, page);
    }

    public WarrantUnderlyersProviderSecurityListType(ProviderId providerId)
    {
        super(providerId);
    }
    //</editor-fold>

    @Override public boolean equalFields(@NonNull SecurityListType other)
    {
        return super.equalFields(other)
                && other instanceof WarrantUnderlyersProviderSecurityListType;
    }
}
