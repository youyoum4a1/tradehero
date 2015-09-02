package com.tradehero.th.fragments.discovery.newsfeed;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.ContainerDTO;
import com.tradehero.common.persistence.DTO;
import java.util.Date;

public class NewsfeedDisplayDTO implements DTO
{
    public int id;
    public Date createdAtUTC;
    public String picture;
    public String name;
    public String body;

    public static class DTOList<T extends NewsfeedDisplayDTO> extends BaseArrayList<T> implements
            DTO,
            ContainerDTO<T, DTOList<T>>
    {
        @Override public DTOList<T> getList()
        {
            return this;
        }
    }
}