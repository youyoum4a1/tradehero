package com.tradehero.th.api.education;

import com.tradehero.common.persistence.DTO;

import org.jetbrains.annotations.NotNull;

public class VideoCategoryDTO implements DTO
{
    public int id;
    public String name;

    @NotNull public VideoCategoryId getVideoCategoryId()
    {
        return new VideoCategoryId(id);
    }
}
