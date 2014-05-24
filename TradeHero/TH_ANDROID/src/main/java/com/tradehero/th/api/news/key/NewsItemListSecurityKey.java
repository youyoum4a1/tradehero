package com.tradehero.th.api.news.key;

import com.tradehero.th.api.security.SecurityIntegerId;

public class NewsItemListSecurityKey extends NewsItemListKey
{
    public final SecurityIntegerId securityIntegerId;

    //<editor-fold desc="Constructors">
    public NewsItemListSecurityKey(SecurityIntegerId securityIntegerId,
            Integer page, Integer perPage)
    {
        super(page, perPage);
        this.securityIntegerId = securityIntegerId;
        if (securityIntegerId == null)
        {
            throw new NullPointerException("SecurityIntegerId cannot be null");
        }
        if (securityIntegerId.key == null)
        {
            throw new NullPointerException("SecurityIntegerId.key cannot be null");
        }
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ securityIntegerId.hashCode();
    }

    @Override protected boolean equalFields(NewsItemListKey other)
    {
        return equalFields((NewsItemListSecurityKey) other);
    }

    protected boolean equalFields(NewsItemListSecurityKey other)
    {
        return super.equalFields(other) &&
                other.securityIntegerId.equals(securityIntegerId);
    }
}
