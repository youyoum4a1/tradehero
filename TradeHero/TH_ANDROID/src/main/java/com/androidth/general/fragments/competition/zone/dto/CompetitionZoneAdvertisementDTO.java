package com.androidth.general.fragments.competition.zone.dto;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.R;
import com.androidth.general.api.competition.AdDTO;
import com.androidth.general.api.competition.ProviderId;
import timber.log.Timber;

public class CompetitionZoneAdvertisementDTO extends CompetitionZoneDTO
{
    @NonNull public final ProviderId providerId;
    @Nullable private final AdDTO adDTO;
    @Nullable public final String bannerImageUrl;
    @DrawableRes @Nullable public final Integer bannerResourceId;

    //<editor-fold desc="Constructors">
    public CompetitionZoneAdvertisementDTO(
            @NonNull Context context,
            @Nullable AdDTO adDTO,
            @NonNull ProviderId providerId)
    {
        super(null, null, null, R.drawable.default_image);
        this.providerId = providerId;
        this.adDTO = adDTO;

        if (adDTO != null)
        {
            bannerImageUrl = adDTO.bannerImageUrl;
            // Ok, this is the only way I found to workaround this problem, converting url to a filename, and manually put 9-patch image
            // with that name to android resource folder.
            String bannerResourceFileName = null;
            try
            {
                bannerResourceFileName = getResourceFileName(bannerImageUrl);
            } catch (StringIndexOutOfBoundsException e)
            {
                Timber.e(e, "When getting %s", bannerImageUrl);
            }
            int tempBannerResId = 0;
            if (bannerResourceFileName != null)
            {
                tempBannerResId = context.getResources().getIdentifier(
                        bannerResourceFileName,
                        "drawable",
                        context.getPackageName());
            }

            if(tempBannerResId > 0){
                bannerResourceId = tempBannerResId;
            }else{
                bannerResourceId = null;
            }
        }
        else
        {
            bannerImageUrl = null;
            bannerResourceId = null;
        }
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (adDTO == null ? 0 : adDTO.bannerImageUrl == null ? Integer.valueOf(0) : adDTO.bannerImageUrl).hashCode();
    }

    @Nullable private static String getResourceFileName(@Nullable String url)
    {
        if (url != null)
        {
            String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());

            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            if (fileName.contains("@"))
            {
                fileName = fileName.substring(0, fileName.lastIndexOf('@'));
            }
            return fileName.replace('-', '_').toLowerCase();
        }
        return null;
    }

    @Nullable public AdDTO getAdDTO()
    {
        return adDTO;
    }

    @Override public String toString()
    {
        return "CompetitionZoneAdvertisementDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + (adDTO == null ? "null" : adDTO.bannerImageUrl) + '\'' +
                '}';
    }
}
