package com.tradehero.th.api.news.key;

public class NewsItemListSocialKey extends NewsItemListKey
{
    public final int categoryId;

    //<editor-fold desc="Constructors">
    public NewsItemListSocialKey(int categoryId,
            Integer page, Integer perPage)
    {
        super(page, perPage);
        this.categoryId = categoryId;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ Integer.valueOf(categoryId).hashCode();
    }

    @Override protected boolean equalFields(NewsItemListKey other)
    {
        return equalFields((NewsItemListSocialKey) other);
    }

    protected boolean equalFields(NewsItemListSocialKey other)
    {
        return super.equalFields(other) &&
                other.categoryId == categoryId;
    }
}
