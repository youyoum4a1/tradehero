package com.tradehero.th.persistence.position;

import com.tradehero.common.persistence.Query;
import com.tradehero.th.api.security.SecurityId;

/** Created with IntelliJ IDEA. User: xavier Date: 10/1/13 Time: 4:53 PM To change this template use File | Settings | File Templates. */
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
