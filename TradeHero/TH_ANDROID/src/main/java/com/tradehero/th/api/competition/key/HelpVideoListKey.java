package com.ayondo.academy.api.competition.key;

import com.tradehero.common.persistence.DTOKey;
import com.ayondo.academy.api.competition.ProviderId;

public class HelpVideoListKey implements DTOKey
{
    private final ProviderId providerId;

    //<editor-fold desc="Constructor">
    public HelpVideoListKey(ProviderId providerId)
    {
        this.providerId = providerId;
        this.validate();
    }
    //</editor-fold>

    public void validate()
    {
        if (this.providerId == null)
        {
            throw new IllegalArgumentException("ProviderId cannot be null");
        }
    }

    public ProviderId getProviderId()
    {
        return providerId;
    }

    @Override public int hashCode()
    {
        return providerId == null ? 0 : providerId.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof HelpVideoListKey) && equals((HelpVideoListKey) other);
    }

    public boolean equals(HelpVideoListKey other)
    {
        return other != null &&
                (providerId == null ? other.providerId == null : providerId.equals(other.providerId));
    }
}
