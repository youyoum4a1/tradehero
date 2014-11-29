package com.tradehero.th.models.share.preference;

import android.support.annotation.NonNull;
import com.tradehero.th.api.social.SocialNetworkEnum;
import org.json.JSONException;
import org.json.JSONObject;

public interface SocialSharePreferenceDTO
{
    boolean isShareEnabled();
    @NonNull JSONObject toJSONObject() throws JSONException;
    @NonNull SocialNetworkEnum getSocialNetworkEnum();
    void setIsShareEnabled(boolean isShareEnabled);
}
