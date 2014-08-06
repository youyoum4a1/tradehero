package com.tradehero.th.api.portfolio;

import android.os.Bundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PagedOwnedPortfolioId extends OwnedPortfolioId
{
    public final static String BUNDLE_KEY_PAGE = PagedOwnedPortfolioId.class.getName() + ".page";

    @Nullable public final Integer page;

    //<editor-fold desc="Constructors">
    public PagedOwnedPortfolioId(int userId, int portfolioId, @Nullable Integer page)
    {
        super(userId, portfolioId);
        this.page = page;
    }

    public PagedOwnedPortfolioId(Bundle args)
    {
        super(args);
        this.page = args.containsKey(BUNDLE_KEY_PAGE) ? args.getInt(BUNDLE_KEY_PAGE) : null;
    }
    //</editor-fold>

    public static boolean isPagedOwnedPortfolioId(@NotNull Bundle args)
    {
        return isOwnedPortfolioId(args)
                && args.containsKey(BUNDLE_KEY_PAGE);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ (page == null ? 0: page.hashCode());
    }

    public boolean equals(@Nullable PagedOwnedPortfolioId other)
    {
        return other != null &&
                super.equals(other) &&
                (page == null ? other.page == null : page.equals(other.page));
    }

    public int compareTo(@NotNull PagedOwnedPortfolioId other)
    {
        if (this == other)
        {
            return 0;
        }

        int parentComp = super.compareTo(other);
        if (parentComp != 0)
        {
            return parentComp;
        }

        if (page == null)
        {
            return other.page == null ? 0 : 1;
        }

        return page.compareTo(other.page);
    }

    @Override protected void putParameters(@NotNull Bundle args)
    {
        super.putParameters(args);
        if (page == null)
        {
            args.remove(BUNDLE_KEY_PAGE);
        }
        else
        {
            args.putInt(BUNDLE_KEY_PAGE, page);
        }
    }

    @Override @NotNull public String toString()
    {
        return String.format("[userId=%d; portfolioId=%d; page=%d]", userId, portfolioId, page);
    }
}
