package com.tradehero.th.api.competition.key;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.security.WarrantType;
import com.tradehero.th.api.security.key.SecurityListType;

public class WarrantProviderSecurityListType extends ProviderSecurityListType
{
    @Nullable private WarrantType warrantType;

    //<editor-fold desc="Constructors">
    public WarrantProviderSecurityListType(
            @NonNull ProviderId providerId,
            @Nullable WarrantType warrantType,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        super(providerId, page, perPage);
        this.warrantType = warrantType;
    }

    public WarrantProviderSecurityListType(
            @NonNull ProviderId providerId,
            @Nullable WarrantType warrantType,
            @Nullable Integer page)
    {
        super(providerId, page);
        this.warrantType = warrantType;
    }

    public WarrantProviderSecurityListType(@NonNull ProviderId providerId, @Nullable WarrantType warrantType)
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

    @Nullable public WarrantType getWarrantType()
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
