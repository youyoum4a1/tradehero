package com.ayondo.academy.api.education;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;

public class VideoCategoryDTO implements DTO
{
    public int id;
    public String name;

    @JsonIgnore
    public int currentPosition;

    @NonNull public VideoCategoryId getVideoCategoryId()
    {
        return new VideoCategoryId(id);
    }
}
