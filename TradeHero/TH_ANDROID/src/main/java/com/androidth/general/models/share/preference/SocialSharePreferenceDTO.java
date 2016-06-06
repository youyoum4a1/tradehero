package com.androidth.general.models.share.preference;

import android.support.annotation.NonNull;
import com.androidth.general.api.social.SocialNetworkEnum;
import org.json.JSONException;
import org.json.JSONObject;

public interface SocialSharePreferenceDTO
{
    boolean isShareEnabled();
    @NonNull JSONObject toJSONObject() throws JSONException;
    @NonNull SocialNetworkEnum getSocialNetworkEnum();
    void setIsShareEnabled(boolean isShareEnabled);
}
