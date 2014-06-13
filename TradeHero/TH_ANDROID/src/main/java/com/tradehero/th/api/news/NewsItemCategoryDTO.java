package com.tradehero.th.api.news;

import com.tradehero.common.persistence.DTO;

public class NewsItemCategoryDTO
        implements DTO
{
    public Integer id;
    public String name;

    public NewsItemCategoryDTO(Integer id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public NewsItemCategoryDTO()
    {
    }
}
