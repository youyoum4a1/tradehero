package com.androidth.general.api.education;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.common.persistence.DTO;

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
