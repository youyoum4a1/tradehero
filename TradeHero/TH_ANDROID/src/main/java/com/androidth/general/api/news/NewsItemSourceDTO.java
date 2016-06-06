package com.androidth.general.api.news;

import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.DTO;

public class NewsItemSourceDTO
    implements DTO
{
    @Nullable public Integer id;
    public String rssSource;
    public String rootName;
    public String url;
    public FbPageDTO fbPage;
    public String imageUrl;
}
