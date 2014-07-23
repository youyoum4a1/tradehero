package com.tradehero.th.api.portfolio;

import android.os.Bundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PerPagedOwnedPortfolioId extends PagedOwnedPortfolioId
{
    public final static String BUNDLE_KEY_PER_PAGE = PerPagedOwnedPortfolioId.class.getName() + ".perPage";

    @Nullable public final Integer perPage;

    //<editor-fold desc="Constructors">
    public PerPagedOwnedPortfolioId(int userId, int portfolioId, @Nullable Integer page, @Nullable Integer perPage)
    {
        super(userId, portfolioId, page);
        this.perPage = perPage;
    }

    public PerPagedOwnedPortfolioId(Bundle args)
    {
        super(args);
        this.perPage = args.containsKey(BUNDLE_KEY_PER_PAGE) ? args.getInt(BUNDLE_KEY_PER_PAGE) : null;
    }
    //</editor-fold>

    public static boolean isPerPagedOwnedPortfolioId(@NotNull Bundle args)
    {
        return isPagedOwnedPortfolioId(args)
                && args.containsKey(BUNDLE_KEY_PER_PAGE);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ (perPage == null ? 0 : perPage.hashCode());
    }

    public boolean equals(@Nullable PerPagedOwnedPortfolioId other)
    {
        return other != null &&
                super.equals(other) &&
                (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    public int compareTo(@NotNull PerPagedOwnedPortfolioId other)
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

        if (perPage == null)
        {
            return other.perPage == null ? 0 : 1;
        }

        return perPage.compareTo(other.perPage);
    }

    @Override protected void putParameters(@NotNull Bundle args)
    {
        super.putParameters(args);
        if (perPage == null)
        {
            args.remove(BUNDLE_KEY_PER_PAGE);
        }
        else
        {
            args.putInt(BUNDLE_KEY_PER_PAGE, perPage);
        }
    }

    @Override @NotNull public String toString()
    {
        return String.format("[userId=%d; portfolioId=%d; page=%d; perPage=%d]", userId, portfolioId, page, perPage);
    }
}
