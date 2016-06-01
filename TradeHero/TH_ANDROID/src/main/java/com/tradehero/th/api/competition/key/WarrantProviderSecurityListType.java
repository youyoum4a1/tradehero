package com.ayondo.academy.api.competition.key;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.api.security.WarrantType;
import com.ayondo.academy.api.security.key.SecurityListType;

public class WarrantProviderSecurityListType extends ProviderSecurityListType
{
    @Nullable public final WarrantType warrantType;

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

    @Override public int hashCode()
    {
        return super.hashCode()
                ^ (warrantType == null ? 0 : warrantType.hashCode());
    }

    @Override public boolean equalFields(@NonNull SecurityListType other)
    {
        return super.equalFields(other)
                && other instanceof WarrantProviderSecurityListType;
    }

    @Override protected boolean equalFields(@NonNull ProviderSecurityListType other)
    {
        return super.equalFields(other)
                && other instanceof WarrantProviderSecurityListType
                && equals((WarrantProviderSecurityListType) other);
    }

    protected boolean equals(@NonNull WarrantProviderSecurityListType other)
    {
        return super.equalFields(other)
                && (this.warrantType == null ? other.warrantType == null : this.warrantType.equals(other.warrantType));
    }
}
