package com.tradehero.th.api.position;

import android.os.Bundle;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 12:50 PM To change this template use File | Settings | File Templates. */
public class FiledPositionId extends OwnedPositionId
{
    public final static String BUNDLE_KEY_PORTFOLIO_ID = OwnedPositionId.class.getName() + ".portfolioId";

    public final Integer portfolioId;

    //<editor-fold desc="Constructors">
    public FiledPositionId(Integer userId, Integer securityId, Integer portfolioId)
    {
        super(userId, securityId);
        this.portfolioId = portfolioId;
    }

    public FiledPositionId(UserBaseKey userBaseKey, SecurityIntegerId securityIntegerId, PortfolioId portfolioId)
    {
        super(userBaseKey, securityIntegerId);
        this.portfolioId = portfolioId.key;
    }

    public FiledPositionId(UserBaseDTO userBaseDTO, SecurityCompactDTO securityCompactDTO, PortfolioId portfolioId)
    {
        super(userBaseDTO, securityCompactDTO);
        this.portfolioId = portfolioId.key;
    }

    public FiledPositionId(Bundle args)
    {
        super(args);
        this.portfolioId = args.containsKey(BUNDLE_KEY_PORTFOLIO_ID) ? args.getInt(BUNDLE_KEY_PORTFOLIO_ID) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ portfolioId.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        if (other == null || !(other instanceof FiledPositionId))
        {
            return false;
        }
        return equals((FiledPositionId) other);
    }

    public boolean equals(FiledPositionId other)
    {
        if (other == null)
        {
            return false;
        }
        return super.equals((OwnedPositionId) other) && portfolioId.equals(other.portfolioId);
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == FiledPositionId.class)
        {
            return compareTo((FiledPositionId) o);
        }
        return o.getClass().getName().compareTo(FiledPositionId.class.getName());
    }

    public int compareTo(FiledPositionId other)
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

        return portfolioId.compareTo(other.portfolioId);
    }

    @Override public boolean isValid()
    {
        return super.isValid() && this.portfolioId != null;
    }

    @Override public void putParameters(Bundle args)
    {
        super.putParameters(args);
        args.putInt(BUNDLE_KEY_PORTFOLIO_ID, portfolioId);
    }

    @Override public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    public PortfolioId getPortfolioId()
    {
        return new PortfolioId(portfolioId);
    }

    @Override public String toString()
    {
        return String.format("[userId=%d; securityId=%d; portfolioId=%d]", userId, securityId, portfolioId);
    }

    @Override public String makeKey()
    {
        return String.format("%d:%d:%d", userId, securityId, portfolioId);
    }
}
