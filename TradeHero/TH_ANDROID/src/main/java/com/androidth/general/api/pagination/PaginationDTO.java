package com.androidth.general.api.pagination;

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

    @Override public String toString()
    {
        return String.format("[page=%d, perPage=%d]", page, perPage);
    }
}
