package com.ayondo.academy.api.education;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTO;

public class VideoDTO implements DTO
{
    public int id;
    public String name;
    public String url;
    public String thumbnail;
    public boolean locked;

    @NonNull public VideoId getVideoId()
    {
        return new VideoId(id);
    }
}
