package com.androidth.general.api.security;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = SecurityTypeDTO.class)

public class SecurityTypeDTO implements DTO
{
    private final long createdAtNanoTime = System.nanoTime();
    public Integer id;
    public String name;
    public String imgUrl;

    public SecurityTypeDTO()
    {
        super();
    }

    public SecurityTypeDTO(@NonNull SecurityTypeDTO other)
    {
        super();
        this.id = other.id;
        this.name = other.name;
        this.imgUrl = other.imgUrl;
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "SecurityTypeDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
