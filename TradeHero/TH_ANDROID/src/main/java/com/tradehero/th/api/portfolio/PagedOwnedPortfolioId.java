package com.tradehero.th.api.portfolio;

import android.os.Bundle;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 3:30 PM To change this template use File | Settings | File Templates. */
public class PagedOwnedPortfolioId extends OwnedPortfolioId
{
    public final static String BUNDLE_KEY_PAGE = PagedOwnedPortfolioId.class.getName() + ".page";

    public final Integer page;

    //<editor-fold desc="Constructors">
    public PagedOwnedPortfolioId(Integer userId, Integer portfolioId, int page)
    {
        super(userId, portfolioId);
        this.page = page;
    }

    public PagedOwnedPortfolioId(UserBaseKey userBaseKey, PortfolioId portfolioId, int page)
    {
        super(userBaseKey, portfolioId);
        this.page = page;
    }

    public PagedOwnedPortfolioId(UserBaseDTO userBaseDTO, PortfolioCompactDTO portfolioCompactDTO, int page)
    {
        super(userBaseDTO, portfolioCompactDTO);
        this.page = page;
    }

    public PagedOwnedPortfolioId(Bundle args)
    {
        super(args);
        this.page = args.containsKey(BUNDLE_KEY_PAGE) ? args.getInt(BUNDLE_KEY_PAGE) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (page == null ? 0: page.hashCode());
    }

    @Override public boolean equals(Object other)
    {
        return (other instanceof PagedOwnedPortfolioId) && equals((PagedOwnedPortfolioId) other);
    }

    public boolean equals(PagedOwnedPortfolioId other)
    {
        return other != null &&
                super.equals(other) &&
                (page == null ? other.page == null : page.equals(other.page));
    }

    @Override public int compareTo(Object o)
    {
        if (o == null)
        {
            return 1;
        }

        if (o.getClass() == PagedOwnedPortfolioId.class)
        {
            return compareTo((PagedOwnedPortfolioId) o);
        }
        return o.getClass().getName().compareTo(PagedOwnedPortfolioId.class.getName());
    }

    public int compareTo(PagedOwnedPortfolioId other)
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

        return page.compareTo(other.page);
    }

    @Override public boolean isValid()
    {
        return super.isValid() && page != null;
    }

    @Override public void putParameters(Bundle args)
    {
        super.putParameters(args);
        args.putInt(BUNDLE_KEY_PAGE, page);
    }

    @Override public String toString()
    {
        return String.format("[userId=%d; portfolioId=%d; page=%d]", userId, portfolioId, page);
    }
}
