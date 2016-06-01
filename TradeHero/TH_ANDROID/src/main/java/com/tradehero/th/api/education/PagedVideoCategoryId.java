package com.ayondo.academy.api.education;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.api.PagedDTOKey;

public class PagedVideoCategoryId extends VideoCategoryId
    implements PagedDTOKey
{
    private static final String BUNDLE_KEY_PAGE = PagedVideoCategoryId.class.getName() + ".page";
    private static final String BUNDLE_KEY_PER_PAGE = PagedVideoCategoryId.class.getName() + ".perPage";

    @Nullable public final Integer page;
    @Nullable public final Integer perPage;

    //<editor-fold desc="Constructors">
    public PagedVideoCategoryId(
            @NonNull Integer id,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        super(id);
        this.page = page;
        this.perPage = perPage;
    }

    public PagedVideoCategoryId(@NonNull Bundle args)
    {
        super(args);
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
        return super.hashCode()
                ^ (page == null ? 0 : page.hashCode())
                ^ (perPage == null ? 0 : perPage.hashCode());
    }

    @Override public boolean equals(Object other)
    {
        return other instanceof PagedVideoCategoryId
                && equalsFields((PagedVideoCategoryId) other);
    }

    protected boolean equalsFields(@NonNull PagedVideoCategoryId other)
    {
        return super.equalsFields(other)
                && (page == null ? other.page == null : page.equals(other.page))
                && (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    @Override @Nullable public Integer getPage()
    {
        return page;
    }
}
