package com.tradehero.th.api.education;

import com.tradehero.common.persistence.DTO;
import org.jetbrains.annotations.NotNull;

public class VideoDTO implements DTO
{
    public int id;
    public String name;
    public String url;
    public String thumbnail;
    public boolean locked;

    @NotNull public VideoId getVideoId()
    {
        return new VideoId(id);
    }
}
