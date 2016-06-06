package com.androidth.general.api.security.key;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.api.PagedDTOKey;

abstract public class SecurityListType implements Comparable<SecurityListType>, PagedDTOKey
{
    @Nullable public final Integer page;
    @Nullable public final Integer perPage;

    //<editor-fold desc="Constructors">
    protected SecurityListType(@NonNull SecurityListType other)
    {
        this.page = other.page;
        this.perPage = other.perPage;
    }

    protected SecurityListType(@Nullable Integer page, @Nullable Integer perPage)
    {
        this.page = page;
        this.perPage = perPage;
        validate();
    }

    protected SecurityListType(@Nullable Integer page)
    {
        this.page = page;
        this.perPage = null;
        validate();
    }

    protected SecurityListType()
    {
        this.page = null;
        this.perPage = null;
        validate();
    }
    //</editor-fold>

    /**
     * It should not be made protected or public as it is called in the constructor.
     */
    private void validate()
    {
        if (page == null && perPage != null)
        {
            throw new NullPointerException("Page cannot be null if perPage is not null");
        }
        else if (page != null && page < 0)
        {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        else if (perPage != null && perPage <= 0)
        {
            throw new IllegalArgumentException("PerPage cannot be zero or negative");
        }
    }

    @Override public int hashCode()
    {
        return (page == null ? 0 : page.hashCode()) ^
                (perPage == null ? 0 : perPage.hashCode());
    }

    @Override public boolean equals(@Nullable Object other)
    {
        if (other == this)
        {
            return true;
        }
        return other instanceof SecurityListType
                && equalFields((SecurityListType) other);
    }

    protected boolean equalFields(@NonNull SecurityListType other)
    {
        return (page == null ? other.page == null : page.equals(other.page))
                && (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    @Override public int compareTo(@NonNull SecurityListType another)
    {
        int pageCompare = page == null ? (another.page == null ? 0 : -1) : page.compareTo(another.page);
        if (pageCompare != 0)
        {
            return pageCompare;
        }

        return perPage == null ? (another.perPage == null ? 0 : -1) : perPage.compareTo(another.perPage);
    }

    @Override @Nullable public Integer getPage()
    {
        return page;
    }

    @Override public String toString()
    {
        return "SecurityListType{" +
                "page=" + page +
                ", perPage=" + perPage +
                '}';
    }
}
