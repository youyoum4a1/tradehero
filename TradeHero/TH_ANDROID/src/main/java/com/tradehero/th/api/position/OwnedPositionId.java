package com.tradehero.th.api.position;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 12:31 PM To change this template use File | Settings | File Templates. */
public class OwnedPositionId implements Comparable, DTOKey<String>
{
    public final static String BUNDLE_KEY_USER_ID = OwnedPositionId.class.getName() + ".userId";
    public final static String BUNDLE_KEY_SECURITY_ID = OwnedPositionId.class.getName() + ".securityId";

    public final Integer userId;
    public final Integer securityId;

    //<editor-fold desc="Constructors">
    public OwnedPositionId(final Integer userId, final Integer securityId)
    {
        this.userId = userId;
        this.securityId = securityId;
    }

    public OwnedPositionId(UserBaseKey userBaseKey, SecurityIntegerId securityIntegerId)
    {
        this.userId = userBaseKey.key;
        this.securityId = securityIntegerId.key;
    }

    public OwnedPositionId(UserBaseDTO userBaseDTO, SecurityCompactDTO securityCompactDTO)
    {
        this.userId = userBaseDTO.id;
        this.securityId = securityCompactDTO.id;
    }

    public OwnedPositionId(Bundle args)
    {
        this.userId = args.containsKey(BUNDLE_KEY_USER_ID) ? args.getInt(BUNDLE_KEY_USER_ID) : null;
        this.securityId = args.containsKey(BUNDLE_KEY_SECURITY_ID) ? args.getInt(BUNDLE_KEY_SECURITY_ID) : null;
    }
    //</editor-fold>


    @Override public int hashCode()
    {
        return userId.hashCode() ^ securityId.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        if (other == null || !(other instanceof OwnedPositionId))
        {
            return false;
        }
        return equals((OwnedPositionId) other);
    }

    public boolean equals(OwnedPositionId other)
{
    if (other == null)
    {
        return false;
    }
    return userId.equals(other.userId) && securityId.equals(other.securityId);
}

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == OwnedPositionId.class)
        {
            return compareTo((OwnedPositionId) o);
        }
        return o.getClass().getName().compareTo(OwnedPositionId.class.getName());
    }

    public int compareTo(OwnedPositionId other)
    {
        if (this == other)
        {
            return 0;
        }

        if (other == null)
        {
            return 1;
        }

        int exchangeComp = userId.compareTo(other.userId);
        if (exchangeComp != 0)
        {
            return exchangeComp;
        }

        return securityId.compareTo(other.securityId);
    }

    public boolean isValid()
    {
        return userId != null && securityId != null;
    }

    public void putParameters(Bundle args)
    {
        args.putInt(BUNDLE_KEY_USER_ID, userId);
        args.putInt(BUNDLE_KEY_SECURITY_ID, securityId);
    }

    public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    public UserBaseKey getUserBaseKey()
    {
        return new UserBaseKey(userId);
    }

    public SecurityIntegerId getSecurityIntegerId()
    {
        return new SecurityIntegerId(securityId);
    }

    @Override public String toString()
    {
        return String.format("[userId=%d; securityId=%d]", userId, securityId);
    }

    @Override public String makeKey()
    {
        return String.format("%d:%d", userId, securityId);
    }
}