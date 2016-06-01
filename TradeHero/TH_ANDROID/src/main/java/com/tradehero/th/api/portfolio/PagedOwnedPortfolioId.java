package com.ayondo.academy.api.portfolio;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    public static boolean isPagedOwnedPortfolioId(@NonNull Bundle args)
    {
        return isOwnedPortfolioId(args)
                && args.containsKey(BUNDLE_KEY_PAGE);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^ (page == null ? 0: page.hashCode());
    }

    @Override protected boolean equalFields(@NonNull OwnedPortfolioId other)
    {
        return (other instanceof PagedOwnedPortfolioId)
                && equalFields((PagedOwnedPortfolioId) other);
    }

    protected boolean equalFields(@NonNull PagedOwnedPortfolioId other)
    {
        return super.equalFields(other) &&
                (page == null ? other.page == null : page.equals(other.page));
    }

    public int compareTo(@NonNull PagedOwnedPortfolioId other)
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

    @Override protected void putParameters(@NonNull Bundle args)
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

    @Override @NonNull public String toString()
    {
        return String.format("[userId=%d; portfolioId=%d; page=%d]", userId, portfolioId, page);
    }
}
