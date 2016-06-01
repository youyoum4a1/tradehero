package com.ayondo.academy.api.discussion.newsfeed;

import android.support.annotation.Nullable;
import com.tradehero.common.persistence.DTO;

public class StockTwitEntitiesDTO implements DTO
{
    @Nullable public ChartDTO chart;
    @Nullable public LinkDTO link;
    @Nullable public VideoDTO video;

    public static class ChartDTO
    {
        public String thumb;
        public String original;
    }

    public static class LinkDTO
    {
        public String link;
    }

    public static class VideoDTO
    {
        public String thumb;
        public String original;
    }
}
