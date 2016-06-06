package com.androidth.general.api.competition.key;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.security.key.SecurityListType;

abstract public class ProviderSecurityListType extends SecurityListType
{
    @NonNull public final ProviderId providerId;

    //<editor-fold desc="Constructors">
    protected ProviderSecurityListType(@NonNull ProviderSecurityListType other)
    {
        super(other);
        this.providerId = other.providerId;
    }

    protected ProviderSecurityListType(@NonNull ProviderId providerId, @Nullable Integer page, @Nullable Integer perPage)
    {
        super(page, perPage);
        this.providerId = providerId;
    }

    protected ProviderSecurityListType(@NonNull ProviderId providerId, @Nullable Integer page)
    {
        super(page);
        this.providerId = providerId;
    }

    protected ProviderSecurityListType(@NonNull ProviderId providerId)
    {
        super();
        this.providerId = providerId;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ providerId.hashCode();
    }

    @Override protected boolean equalFields(@NonNull SecurityListType other)
    {
        return other instanceof ProviderSecurityListType
                && equalFields((ProviderSecurityListType) other);
    }

    protected boolean equalFields(@NonNull ProviderSecurityListType other)
    {
        return super.equalFields(other)
                && providerId.equals(other.providerId);
    }

    //<editor-fold desc="Comparable">
    @Override public int compareTo(@NonNull SecurityListType another)
    {
        if (!ProviderSecurityListType.class.isInstance(another))
        {
            // TODO is it very expensive?
            return ProviderSecurityListType.class.getName().compareTo(((Object) another).getClass().getName());
        }

        return compareTo(ProviderSecurityListType.class.cast(another));
    }

    public int compareTo(@Nullable ProviderSecurityListType another)
    {
        if (another == null)
        {
            return 1;
        }
        int providerIdCompare = providerId.compareTo(another.providerId);
        if (providerIdCompare != 0)
        {
            return providerIdCompare;
        }
        return super.compareTo(another);
    }
    //</editor-fold>
}
