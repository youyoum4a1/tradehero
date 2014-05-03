package com.tradehero.th.api.news;

import com.tradehero.common.persistence.DTO;


public class NewsItemSourceDTO
    implements DTO
{
    public Integer id;
    public String rssSource;
    public String rootName;
    public String url;
    public FbPageDTO fbPage;
    public String imageUrl;
}
