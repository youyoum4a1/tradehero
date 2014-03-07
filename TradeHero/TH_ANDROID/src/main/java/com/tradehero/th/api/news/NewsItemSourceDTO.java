package com.tradehero.th.api.news;

import com.tradehero.common.persistence.DTO;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/6/14 Time: 4:06 PM Copyright (c) TradeHero
 */
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
