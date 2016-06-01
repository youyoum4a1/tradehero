package com.ayondo.academy.api.news.key;

import com.tradehero.common.persistence.DTOKey;

public class NewsItemListKey implements DTOKey
{
    public final Integer page;
    public final Integer perPage;

    //<editor-fold desc="Constructors">
    public NewsItemListKey(Integer page, Integer perPage)
    {
        this.page = page;
        this.perPage = perPage;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return ((page == null ? 0 : page) * 31 + (perPage == null ? 0 : perPage)) * 31;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override public boolean equals(Object other)
    {
        return equalClass(other) && equalFields((NewsItemListKey) other);
    }

    protected boolean equalClass(Object other)
    {
        return other != null && other.getClass().equals(getClass());
    }

    protected boolean equalFields(NewsItemListKey other)
    {
        return (page == null ? other.page == null : page.equals(other.page)) &&
                (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    @Override public String toString()
    {
        return String.format("[page=%d, perPager=%d] - ", page, perPage) + getClass().toString();
    }
}
