package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.security.SecurityId;

public class SecurityPositionDetailQuery extends Query
{
    public SecurityPositionDetailQuery ()
    {
    }

    public SecurityPositionDetailQuery(SecurityId securityId)
    {
        setSecurityId(securityId);
    }

    public SecurityId getSecurityId()
    {
        return (SecurityId) getId();
    }

    public void setSecurityId(SecurityId securityId)
    {
        setId(securityId);
    }
}
