package com.tradehero.th.api.portfolio;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.users.UserBaseDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 12:19 PM To change this template use File | Settings | File Templates. */
public class OwnedPortfolioId  implements Comparable, DTOKey<String>
{
    public final static String BUNDLE_KEY_USER_ID = OwnedPortfolioId.class.getName() + ".userId";
    public final static String BUNDLE_KEY_PORTFOLIO_ID = OwnedPortfolioId.class.getName() + ".portfolioId";

    public final Integer userId;
    public final Integer portfolioId;

    //<editor-fold desc="Constructors">
    public OwnedPortfolioId(final Integer userId, final Integer portfolioId)
    {
        this.userId = userId;
        this.portfolioId = portfolioId;
    }

    public OwnedPortfolioId(UserBaseDTO userBaseDTO, PortfolioCompactDTO portfolioCompactDTO)
    {
        this.userId = userBaseDTO.id;
        this.portfolioId = portfolioCompactDTO.id;
    }

    public OwnedPortfolioId(Bundle args)
    {
        this.userId = args.containsKey(BUNDLE_KEY_USER_ID) ? args.getInt(BUNDLE_KEY_USER_ID) : null;
        this.portfolioId = args.containsKey(BUNDLE_KEY_PORTFOLIO_ID) ? args.getInt(BUNDLE_KEY_PORTFOLIO_ID) : null;
    }
    //</editor-fold>


    @Override public int hashCode()
    {
        return userId.hashCode() ^ portfolioId.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        if (other == null || !(other instanceof OwnedPortfolioId))
        {
            return false;
        }
        return equals((OwnedPortfolioId) other);
    }

    public boolean equals(OwnedPortfolioId other)
    {
        if (other == null)
        {
            return false;
        }
        return userId.equals(other.userId) && portfolioId.equals(other.portfolioId);
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == OwnedPortfolioId.class)
        {
            return compareTo((OwnedPortfolioId) o);
        }
        return o.getClass().getName().compareTo(OwnedPortfolioId.class.getName());
    }

    public int compareTo(OwnedPortfolioId other)
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

        return portfolioId.compareTo(other.portfolioId);
    }

    public boolean isValid()
    {
        return userId != null && portfolioId != null;
    }

    public void putParameters(Bundle args)
    {
        args.putInt(BUNDLE_KEY_USER_ID, userId);
        args.putInt(BUNDLE_KEY_PORTFOLIO_ID, portfolioId);
    }

    public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    @Override public String toString()
    {
        return String.format("[userId=%d; portfolioId=%d]", userId, portfolioId);
    }

    @Override public String makeKey()
    {
        return String.format("%d:%d", userId, portfolioId);
    }
}
