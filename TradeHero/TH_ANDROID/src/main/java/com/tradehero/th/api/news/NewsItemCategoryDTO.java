package com.tradehero.th.api.news;

import com.tradehero.common.persistence.DTO;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/6/14 Time: 4:04 PM Copyright (c) TradeHero
 */
public class NewsItemCategoryDTO
    implements DTO
{
    public Integer id;
    public String name;

    public NewsItemCategoryDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public NewsItemCategoryDTO() {
    }
}
