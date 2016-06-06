package com.androidth.general.fragments.discovery.newsfeed;

import com.androidth.general.common.api.BaseArrayList;
import com.androidth.general.common.persistence.ContainerDTO;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.discussion.newsfeed.NewsfeedDTO;
import com.androidth.general.api.discussion.newsfeed.NewsfeedDiscussionDTO;
import com.androidth.general.api.discussion.newsfeed.NewsfeedNewsDTO;
import com.androidth.general.api.discussion.newsfeed.NewsfeedStockTwitDTO;
import java.util.Date;
import org.ocpsoft.prettytime.PrettyTime;

public abstract class NewsfeedDisplayDTO implements DTO
{
    public final int id;
    public final Date createdAtUTC;
    public final String time;
    public final String picture;
    public final String name;

    public NewsfeedDisplayDTO(NewsfeedDTO newsfeedDTO, PrettyTime prettyTime)
    {
        this.id = newsfeedDTO.id;
        this.createdAtUTC = newsfeedDTO.createdAtUTC;
        this.time = prettyTime.format(this.createdAtUTC);
        this.picture = newsfeedDTO.picture;
        this.name = newsfeedDTO.displayName;
    }

    public abstract String getBody();

    public static class DTOList<T extends NewsfeedDisplayDTO> extends BaseArrayList<T> implements
            DTO,
            ContainerDTO<T, DTOList<T>>
    {
        public DTOList(int initialCapacity)
        {
            super(initialCapacity);
        }

        @Override public DTOList<T> getList()
        {
            return this;
        }
    }

    public static NewsfeedDisplayDTO from(NewsfeedDTO newsfeedDTO, PrettyTime prettyTime)
    {
        if (newsfeedDTO instanceof NewsfeedNewsDTO)
        {
            return new NewsfeedNewsDisplayDTO(((NewsfeedNewsDTO) newsfeedDTO), prettyTime);
        }
        else if (newsfeedDTO instanceof NewsfeedStockTwitDTO)
        {
            return new NewsfeedStockTwitDisplayDTO(((NewsfeedStockTwitDTO) newsfeedDTO), prettyTime);
        }
        else if (newsfeedDTO instanceof NewsfeedDiscussionDTO)
        {
            return new NewsfeedDiscussionDisplayDTO(((NewsfeedDiscussionDTO) newsfeedDTO), prettyTime);
        }
        else
        {
            throw new RuntimeException("Unhandled type " + newsfeedDTO.getClass().getName());
        }
    }
}