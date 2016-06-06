package com.androidth.general.api.discussion.newsfeed;

import com.androidth.general.common.api.PagedDTOKey;

public class NewsfeedPagedDTOKey implements PagedDTOKey
{
    public final String countryCode;
    public final String languageCode;
    public final int page;
    public final int perPage;

    public NewsfeedPagedDTOKey(String countryCode, String languageCode, int page, int perPage)
    {
        this.countryCode = countryCode;
        this.languageCode = languageCode;
        this.page = page;
        this.perPage = perPage;
    }

    @Override public Integer getPage()
    {
        return page;
    }
}