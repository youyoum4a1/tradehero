package com.ayondo.academy.models.fastfill.jumio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class NetverifyScanImagesDTO
{
    @NonNull private final Date timestamp;
    @NonNull private final NetverifyScanReference scanReference;
    @Nullable private final List<NetverifyScanImageShortDTO> images;

    public NetverifyScanImagesDTO(
            @JsonProperty("timestamp") @NonNull Date timestamp,
            @JsonProperty("scanReference") @NonNull NetverifyScanReference scanReference,
            @JsonProperty("images") @Nullable List<NetverifyScanImageShortDTO> images)
    {
        this.timestamp = timestamp;
        this.scanReference = scanReference;
        if (images == null)
        {
            this.images = null;
        }
        else
        {
            this.images = Collections.unmodifiableList(images);
        }
    }

    @NonNull public Date getTimestamp()
    {
        return timestamp;
    }

    @NonNull public NetverifyScanReference getScanReference()
    {
        return scanReference;
    }

    @Nullable public List<NetverifyScanImageShortDTO> getImages()
    {
        return images;
    }
}
