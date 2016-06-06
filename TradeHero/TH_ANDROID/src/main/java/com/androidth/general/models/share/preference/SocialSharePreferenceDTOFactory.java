package com.androidth.general.models.share.preference;

import android.support.annotation.NonNull;
import com.androidth.general.api.social.SocialNetworkEnum;
import org.json.JSONException;
import org.json.JSONObject;

public class SocialSharePreferenceDTOFactory
{
    @NonNull public static SocialSharePreferenceDTO create(@NonNull String jsonString) throws JSONException
    {
        return create(new JSONObject(jsonString));
    }

    @NonNull public static SocialSharePreferenceDTO create(@NonNull JSONObject jsonObject) throws JSONException
    {
        SocialNetworkEnum networkEnum = SocialNetworkEnum.valueOf(jsonObject.getString(BaseSocialSharePreferenceDTO.KEY_SOCIAL_NETWORK_ENUM));
        boolean isShareEnabled = jsonObject.getBoolean(BaseSocialSharePreferenceDTO.KEY_IS_SHARE_ENABLED);

        return create(networkEnum, isShareEnabled);
    }

    @NonNull public static SocialSharePreferenceDTO create(SocialNetworkEnum socialNetworkEnum, boolean isShareEnabled)
    {
        SocialSharePreferenceDTO socialSharePreferenceDTO;

        switch (socialNetworkEnum)
        {
            case FB:
                socialSharePreferenceDTO = new FacebookSharePreferenceDTO(isShareEnabled);
                break;
            case LN:
                socialSharePreferenceDTO = new LinkedInSharePreferenceDTO(isShareEnabled);
                break;
            case TW:
                socialSharePreferenceDTO = new TwitterSharePreferenceDTO(isShareEnabled);
                break;
            case WB:
                socialSharePreferenceDTO = new WeiBoSharePreferenceDTO(isShareEnabled);
                break;
            case WECHAT:
                socialSharePreferenceDTO = new WeChatSharePreferenceDTO(isShareEnabled);
                break;
            default:
                throw new IllegalStateException("Unhandled type: " + socialNetworkEnum.getName());
        }

        return socialSharePreferenceDTO;
    }
}
