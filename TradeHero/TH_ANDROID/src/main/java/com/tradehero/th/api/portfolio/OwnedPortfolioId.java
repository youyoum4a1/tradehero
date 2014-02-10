package com.tradehero.th.api.portfolio;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 12:19 PM To change this template use File | Settings | File Templates. */
public class OwnedPortfolioId  implements Comparable, DTOKey
{
    public final static String BUNDLE_KEY_USER_ID = OwnedPortfolioId.class.getName() + ".userId";
    public final static String BUNDLE_KEY_PORTFOLIO_ID = OwnedPortfolioId.class.getName() + ".portfolioId";

    public Integer userId;
    public Integer portfolioId;

    //<editor-fold desc="Constructors">
    public OwnedPortfolioId()
    {
    }

    public OwnedPortfolioId(final Integer userId, final Integer portfolioId)
    {
        this.userId = userId;
        this.portfolioId = portfolioId;
    }

    public OwnedPortfolioId(UserBaseKey userBaseKey, PortfolioId portfolioId)
    {
        this.userId = userBaseKey.key;
        this.portfolioId = portfolioId.key;
    }

    public OwnedPortfolioId(OwnedPortfolioId ownedPortfolioId)
    {
        this.userId = ownedPortfolioId.userId;
        this.portfolioId = ownedPortfolioId.portfolioId;
    }

    public OwnedPortfolioId(UserBaseKey userBaseKey, PortfolioCompactDTO portfolioCompactDTO)
    {
        this.userId = userBaseKey.key;
        this.portfolioId = portfolioCompactDTO.id;
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
        return (userId == null ? 0 : userId.hashCode()) ^
                (portfolioId == null ? 0 : portfolioId.hashCode());
    }

    @Override public boolean equals(Object other)
    {
        return getClass().isInstance(other) && equals(getClass().cast(other));
    }

    public boolean equals(OwnedPortfolioId other)
    {
        return (other != null) &&
                (userId == null ? other.userId == null : userId.equals(other.userId)) &&
                (portfolioId == null ? other.portfolioId == null : portfolioId.equals(other.portfolioId));
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == getClass())
        {
            return compareTo(getClass().cast(o));
        }
        return o.getClass().getName().compareTo(getClass().getName());
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

    protected void putParameters(Bundle args)
    {
        if (userId != null)
        {
            args.putInt(BUNDLE_KEY_USER_ID, userId);
        }
        if (portfolioId != null)
        {
            args.putInt(BUNDLE_KEY_PORTFOLIO_ID, portfolioId);
        }
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

    public PortfolioId getPortfolioId()
    {
        return new PortfolioId(portfolioId);
    }

    @Override public String toString()
    {
        return "OwnedPortfolioId{" +
                "portfolioId=" + portfolioId +
                ", userId=" + userId +
                '}';
    }
}
