package com.tradehero.th.api.education;

import android.os.Bundle;

import com.tradehero.common.api.PagedDTOKey;
import com.tradehero.common.persistence.DTOKey;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PagedVideoCategories implements DTOKey, PagedDTOKey
{
    private static final String BUNDLE_KEY_PAGE = PagedVideoCategories.class.getName() + ".page";
    private static final String BUNDLE_KEY_PER_PAGE = PagedVideoCategories.class.getName() + ".perPage";

    @Nullable public final Integer page;
    @Nullable public final Integer perPage;

    //<editor-fold desc="Constructors">
    public PagedVideoCategories(
            @NotNull Integer id,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        super();
        this.page = page;
        this.perPage = perPage;
    }

    public PagedVideoCategories(@NotNull Bundle args)
    {
        super();
        if (args.containsKey(BUNDLE_KEY_PAGE))
        {
            this.page = args.getInt(BUNDLE_KEY_PAGE);
        }
        else
        {
            this.page = null;
        }
        if (args.containsKey(BUNDLE_KEY_PER_PAGE))
        {
            this.perPage = args.getInt(BUNDLE_KEY_PER_PAGE);
        }
        else
        {
            this.perPage = null;
        }
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return (page == null ? 0 : page.hashCode())
                ^ (perPage == null ? 0 : perPage.hashCode());
    }

    @Override public boolean equals(Object other)
    {
        return other instanceof PagedVideoCategories
                && equalsFields((PagedVideoCategories) other);
    }

    protected boolean equalsFields(@NotNull PagedVideoCategories other)
    {
        return (page == null ? other.page == null : page.equals(other.page))
                && (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    @Override @Nullable public Integer getPage()
    {
        return page;
    }
}
