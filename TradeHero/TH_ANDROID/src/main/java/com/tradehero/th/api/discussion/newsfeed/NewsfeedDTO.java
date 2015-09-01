package com.tradehero.th.api.discussion.newsfeed;

import com.tradehero.common.persistence.DTO;

public class NewsfeedDTO implements DTO
{
    public int id;

    public NewsfeedKey getKey()
    {
        return new NewsfeedKey(id);
    }
}
