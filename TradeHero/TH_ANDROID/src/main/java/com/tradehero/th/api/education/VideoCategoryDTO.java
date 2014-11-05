package com.tradehero.th.api.education;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;

import android.support.annotation.NonNull;

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
