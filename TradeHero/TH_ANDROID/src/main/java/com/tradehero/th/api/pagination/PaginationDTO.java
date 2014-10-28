package com.tradehero.th.api.pagination;

public class PaginationDTO
{
    public Integer page;
    public Integer perPage;

    public PaginationDTO() {}

    public PaginationDTO(Integer page, Integer perPage)
    {
        this.page = page;
        this.perPage = perPage;
    }
}
