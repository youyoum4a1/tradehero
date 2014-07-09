package com.tradehero.th.models.share.preference;

import com.tradehero.th.api.social.SocialNetworkEnum;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public interface SocialSharePreferenceDTO
{
    boolean isShareEnabled();
    JSONObject toJSONObject() throws JSONException;
    @NotNull SocialNetworkEnum getSocialNetworkEnum();
    void setIsShareEnabled(boolean isShareEnabled);
}
