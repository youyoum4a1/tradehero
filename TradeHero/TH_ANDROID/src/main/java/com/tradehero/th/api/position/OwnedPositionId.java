package com.tradehero.th.api.position;

import android.os.Bundle;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.users.UserBaseKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 12:50 PM To change this template use File | Settings | File Templates. */
public class OwnedPositionId extends OwnedPortfolioId
{
    public final static String BUNDLE_KEY_POSITION_ID = OwnedPositionId.class.getName() + ".portfolioId";
    public final static String BUNDLE_KEY_SECURITY_ID = OwnedPositionId.class.getName() + ".securityId";

    public final Integer positionId;
    public final Integer securityId;

    //<editor-fold desc="Constructors">
    public OwnedPositionId(Integer userId, Integer portfolioId, Integer positionId, Integer securityId)
    {
        super(userId, portfolioId);
        this.positionId = positionId;
        this.securityId = securityId;
    }

    public OwnedPositionId(UserBaseKey userBaseKey, PortfolioId portfolioId, Integer positionId, Integer securityId)
    {
        super(userBaseKey, portfolioId);
        this.positionId = positionId;
        this.securityId = securityId;
    }

    public OwnedPositionId(Bundle args)
    {
        super(args);
        this.positionId = args.containsKey(BUNDLE_KEY_POSITION_ID) ? args.getInt(BUNDLE_KEY_POSITION_ID) : null;
        this.securityId = args.containsKey(BUNDLE_KEY_SECURITY_ID) ? args.getInt(BUNDLE_KEY_SECURITY_ID) : null;
    }

    public OwnedPositionId(OwnedPositionId ownedPositionId)
    {
        super(ownedPositionId.getUserBaseKey(), ownedPositionId.getPortfolioId());
        this.positionId = ownedPositionId.positionId;
        this.securityId = ownedPositionId.securityId;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ portfolioId.hashCode() ^ securityId.hashCode();
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
        return super.equals((OwnedPortfolioId) other) && positionId.equals(other.positionId) && securityId.equals(other.securityId);
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

        int parentComp = super.compareTo(other);
        if (parentComp != 0)
        {
            return parentComp;
        }

        return positionId.compareTo(other.positionId);
    }

    @Override public boolean isValid()
    {
        return super.isValid() && this.positionId != null && this.securityId != null;
    }

    @Override public void putParameters(Bundle args)
    {
        super.putParameters(args);
        args.putInt(BUNDLE_KEY_POSITION_ID, positionId);
        args.putInt(BUNDLE_KEY_SECURITY_ID, securityId);
    }

    @Override public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    public SecurityIntegerId getSecurityIntegerId()
    {
        return new SecurityIntegerId(securityId);
    }

    @Override public String toString()
    {
        return String.format("[userId=%d; portfolioId=%d; ownedPositionId=%d; securityId=%d]", userId, portfolioId, positionId, securityId);
    }

    @Override public String makeKey()
    {
        return String.format("%d:%d:%d:%d", userId, portfolioId, positionId, securityId);
    }
}
