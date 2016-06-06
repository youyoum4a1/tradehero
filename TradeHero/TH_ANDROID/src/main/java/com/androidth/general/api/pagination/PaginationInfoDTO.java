package com.androidth.general.api.pagination;

public class PaginationInfoDTO
{
    public PaginationDTO prev;
    public PaginationDTO next;

    @Override public String toString()
    {
        return String.format("[prev=%s, next=%s]", prev, next);
    }
}
