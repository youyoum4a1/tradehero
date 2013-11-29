package com.tradehero.th.api.portfolio;

import android.os.Bundle;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 3:30 PM To change this template use File | Settings | File Templates. */
public class PerPagedOwnedPortfolioId extends PagedOwnedPortfolioId
{
    public final static String BUNDLE_KEY_PER_PAGE = PerPagedOwnedPortfolioId.class.getName() + ".perPage";

    public final Integer perPage;

    //<editor-fold desc="Constructors">
    public PerPagedOwnedPortfolioId(Integer userId, Integer portfolioId, int page, int perPage)
    {
        super(userId, portfolioId, page);
        this.perPage = perPage;
    }

    public PerPagedOwnedPortfolioId(UserBaseKey userBaseKey, PortfolioId portfolioId, int page, int perPage)
    {
        super(userBaseKey, portfolioId, page);
        this.perPage = perPage;
    }

    public PerPagedOwnedPortfolioId(UserBaseDTO userBaseDTO, PortfolioCompactDTO portfolioCompactDTO, int page, int perPage)
    {
        super(userBaseDTO, portfolioCompactDTO, page);
        this.perPage = perPage;
    }

    public PerPagedOwnedPortfolioId(Bundle args)
    {
        super(args);
        this.perPage = args.containsKey(BUNDLE_KEY_PER_PAGE) ? args.getInt(BUNDLE_KEY_PER_PAGE) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (perPage == null ? 0 : perPage.hashCode());
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof PerPagedOwnedPortfolioId) && equals((PerPagedOwnedPortfolioId) other);
    }

    public boolean equals(PerPagedOwnedPortfolioId other)
    {
        return other != null &&
                super.equals(other) &&
                (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == PerPagedOwnedPortfolioId.class)
        {
            return compareTo((PerPagedOwnedPortfolioId) o);
        }
        return o.getClass().getName().compareTo(PerPagedOwnedPortfolioId.class.getName());
    }

    public int compareTo(PerPagedOwnedPortfolioId other)
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

        return perPage.compareTo(other.perPage);
    }

    @Override public boolean isValid()
    {
        return super.isValid() && perPage != null;
    }

    @Override protected void putParameters(Bundle args)
    {
        super.putParameters(args);
        args.putInt(BUNDLE_KEY_PER_PAGE, perPage);
    }

    @Override public String toString()
    {
        return String.format("[userId=%d; portfolioId=%d; page=%d; perPage=%d]", userId, portfolioId, page, perPage);
    }
}
