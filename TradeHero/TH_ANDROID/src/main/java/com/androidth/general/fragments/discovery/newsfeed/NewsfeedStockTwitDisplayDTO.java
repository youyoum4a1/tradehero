package com.androidth.general.fragments.discovery.newsfeed;

import com.androidth.general.api.discussion.newsfeed.NewsfeedStockTwitDTO;
import com.androidth.general.api.discussion.newsfeed.StockTwitEntitiesDTO;
import org.ocpsoft.prettytime.PrettyTime;

public class NewsfeedStockTwitDisplayDTO extends NewsfeedDisplayDTO
{
    private final String message;
    public final String heroImage;
    public final String link;

    public NewsfeedStockTwitDisplayDTO(NewsfeedStockTwitDTO newsfeedDTO, PrettyTime prettyTime)
    {
        super(newsfeedDTO, prettyTime);
        this.message = newsfeedDTO.message;
        if (newsfeedDTO.entities != null)
        {
            StockTwitEntitiesDTO entitiesDTO = newsfeedDTO.entities;
            if (entitiesDTO.video != null)
            {
                this.heroImage = entitiesDTO.video.thumb;
                this.link = entitiesDTO.video.original;
            }
            else if (entitiesDTO.chart != null)
            {
                this.heroImage = entitiesDTO.chart.thumb;
                this.link = entitiesDTO.chart.original;
            }
            else if (entitiesDTO.link != null)
            {
                this.heroImage = null;
                this.link = entitiesDTO.link.link;
            }
            else
            {
                this.heroImage = null;
                this.link = null;
            }
        }
        else
        {
            this.heroImage = null;
            this.link = null;
        }
    }

    @Override public String getBody()
    {
        return message;
    }
}
