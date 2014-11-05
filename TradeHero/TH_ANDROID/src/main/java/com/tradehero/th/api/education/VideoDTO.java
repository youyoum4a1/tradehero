package com.tradehero.th.api.education;

import com.tradehero.common.persistence.DTO;

import android.support.annotation.NonNull;

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
