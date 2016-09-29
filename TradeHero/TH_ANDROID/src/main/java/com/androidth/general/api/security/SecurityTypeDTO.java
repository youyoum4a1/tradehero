package com.androidth.general.api.security;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = SecurityTypeDTO.class)

public class SecurityTypeDTO implements DTO
{
    public static final String BUNDLE_KEY_NAME = SecurityTypeDTO.class.getName() + ".name";
    private final long createdAtNanoTime = System.nanoTime();
    @JsonProperty("Id") public Integer id;
    @JsonProperty("Name") public String name;
    @JsonProperty("ImageUrl") public String imageUrl;
    @JsonProperty("IsEnabled") public Boolean isEnabled;

    public SecurityTypeDTO()
    {
        super();
    }

    public SecurityTypeDTO(@NonNull SecurityTypeDTO other)
    {
        super();
        this.id = other.id;
        this.name = other.name;
        this.imageUrl = other.imageUrl;
        this.isEnabled = other.isEnabled;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    @Override
    public String toString() {
        return "SecurityTypeDTO{" +
                "createdAtNanoTime=" + createdAtNanoTime +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", isEnabled=" + isEnabled +
                '}';
    }
}
