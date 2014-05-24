package com.tradehero.th.api.news.key;

public class NewsItemListRegionalKey extends NewsItemListKey
{
    public final String countryCode;
    public final String languageCode;

    //<editor-fold desc="Constructors">
    public NewsItemListRegionalKey(String countryCode,
            String languageCode,
            Integer page, Integer perPage)
    {
        super(page, perPage);
        this.countryCode = countryCode;
        this.languageCode = languageCode;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (countryCode == null ? 0 : countryCode.hashCode()) ^
                (languageCode == null ? 0 : languageCode.hashCode());
    }

    @Override protected boolean equalFields(NewsItemListKey other)
    {
        return equalFields((NewsItemListRegionalKey) other);
    }

    protected boolean equalFields(NewsItemListRegionalKey other)
    {
        return super.equalFields(other) &&
                (countryCode == null ? other.countryCode == null : countryCode.equals(other.countryCode)) ^
                        (languageCode == null ? other.languageCode == null : languageCode.equals(other.languageCode));
    }
}
