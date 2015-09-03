package com.tradehero.th.api.discussion.newsfeed;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tradehero.common.persistence.DTO;
import java.util.Date;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = NewsfeedDTO.class,
        property = "newsfeedType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NewsfeedStockTwitDTO.class, name = NewsfeedStockTwitDTO.DTO_DESERIALISING_TYPE),
        @JsonSubTypes.Type(value = NewsfeedNewsDTO.class, name = NewsfeedNewsDTO.DTO_DESERIALISING_TYPE),
})
public class NewsfeedDTO implements DTO
{
    public int id;
    public Date createdAtUTC;
    public String picture;
    public String displayName;
    public String body;

    public NewsfeedKey getKey()
    {
        return new NewsfeedKey(id);
    }
}