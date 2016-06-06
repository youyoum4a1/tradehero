package com.androidth.general.api.competition;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.R;
import com.androidth.general.api.competition.key.ProviderDisplayCellId;

public class ProviderDisplayCellDTO implements DTO
{
    public int id;
    public int providerId;
    @Nullable public String title;
    @Nullable public String subtitle;
    @Nullable public String imageUrl;
    @Nullable public String redirectUrl;

    @JsonIgnore
    public ProviderDisplayCellId getProviderDisplayCellId()
    {
        return new ProviderDisplayCellId(id);
    }

    @Override public int hashCode()
    {
        int hash = 7;
        hash = 31 * hash + id;
        hash = 31 * hash + providerId;
        hash = 31 * hash + (title == null ? 0 : title.hashCode());
        hash = 31 * hash + (subtitle == null ? 0 : subtitle.hashCode());
        hash = 31 * hash + (imageUrl == null ? 0 : imageUrl.hashCode());
        hash = 31 * hash + (redirectUrl == null ? 0 : redirectUrl.hashCode());
        return hash;
    }

    @Override public boolean equals(@Nullable Object o)
    {
        if (o == null)
        {
            return false;
        }
        if (o == this)
        {
            return true;
        }
        if (o instanceof ProviderDisplayCellDTO)
        {
            ProviderDisplayCellDTO other = (ProviderDisplayCellDTO) o;
            return other.id == this.id
                    && other.providerId == this.providerId
                    && (other.title == null ? this.title == null : other.title.equals(this.title))
                    && (other.subtitle == null ? this.subtitle == null : other.subtitle.equals(this.subtitle))
                    && (other.imageUrl == null ? this.imageUrl == null : other.imageUrl.equals(this.imageUrl))
                    && (other.redirectUrl == null ? this.redirectUrl == null : other.redirectUrl.equals(this.redirectUrl));

        }
        return false;
    }

    @Nullable public String getNonEmptyImageUrl()
    {
        return imageUrl == null || imageUrl.isEmpty() ? null : imageUrl;
    }

    @Nullable public String extractRedirectUrl(@NonNull Resources resources)
    {
        if (redirectUrl != null)
        {
            Uri uri = Uri.parse(redirectUrl);
            if (uri.getScheme().equals(resources.getString(R.string.intent_scheme)))
            {
                if (uri.getHost().equalsIgnoreCase(resources.getString(R.string.intent_host_web)))
                {
                    return uri.getQueryParameter("url");
                }
            }
        }
        return redirectUrl;
    }

    @Override public String toString()
    {
        return "ProviderDisplayCellDTO{" +
                "id=" + id +
                ", providerId='" + providerId + '\'' +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                '}';
    }
}
