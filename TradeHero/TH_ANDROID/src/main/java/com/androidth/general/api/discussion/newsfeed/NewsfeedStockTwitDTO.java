package com.androidth.general.api.discussion.newsfeed;

public class NewsfeedStockTwitDTO extends NewsfeedDTO
{
    public static final String DTO_DESERIALISING_TYPE = "stocktwit";
    public String message;
    public StockTwitEntitiesDTO entities;
}
