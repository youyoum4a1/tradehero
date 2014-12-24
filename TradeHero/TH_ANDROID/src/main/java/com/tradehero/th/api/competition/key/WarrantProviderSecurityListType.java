package com.tradehero.th.api.competition.key;

import android.support.annotation.NonNull;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.security.WarrantType;
import com.tradehero.th.api.security.key.SecurityListType;

public class WarrantProviderSecurityListType extends ProviderSecurityListType
{
    private WarrantType warrantType;

    //<editor-fold desc="Constructors">
    public WarrantProviderSecurityListType(ProviderId providerId, WarrantType warrantType, Integer page, Integer perPage)
    {
        super(providerId, page, perPage);
        this.warrantType = warrantType;
    }

    public WarrantProviderSecurityListType(ProviderId providerId, WarrantType warrantType, Integer page)
    {
        super(providerId, page);
        this.warrantType = warrantType;
    }

    public WarrantProviderSecurityListType(ProviderId providerId, WarrantType warrantType)
    {
        super(providerId);
        this.warrantType = warrantType;
    }
    //</editor-fold>

    @Override public boolean equals(@NonNull SecurityListType other)
    {
        return super.equals(other)
                && other instanceof WarrantProviderSecurityListType;
    }

    public WarrantType getWarrantType()
    {
        return warrantType;
    }

    @Override
    public boolean equals(@NonNull ProviderSecurityListType other)
    {
        return super.equals(other)
                && other instanceof WarrantProviderSecurityListType
                && equals((WarrantProviderSecurityListType) other);
    }

    protected boolean equals(@NonNull WarrantProviderSecurityListType other)
    {
        return !((other.warrantType != null && this.warrantType == null) || other.warrantType == null && this.warrantType != null)
                && this.warrantType != null
                && this.warrantType.equals(other.warrantType);
    }
}
