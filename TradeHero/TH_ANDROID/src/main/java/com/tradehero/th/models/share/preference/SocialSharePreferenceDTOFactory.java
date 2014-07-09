package com.tradehero.th.models.share.preference;

import com.tradehero.th.api.social.SocialNetworkEnum;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class SocialSharePreferenceDTOFactory
{
    @Inject public SocialSharePreferenceDTOFactory()
    {
        super();
    }

    @Nullable public SocialSharePreferenceDTO create(@NotNull String jsonString) throws JSONException
    {
        return create(new JSONObject(jsonString));
    }

    @Nullable public SocialSharePreferenceDTO create(@NotNull JSONObject jsonObject) throws JSONException
    {
        SocialNetworkEnum networkEnum = SocialNetworkEnum.valueOf(jsonObject.getString(BaseSocialSharePreferenceDTO.KEY_SOCIAL_NETWORK_ENUM));
        boolean isShareEnabled = jsonObject.getBoolean(BaseSocialSharePreferenceDTO.KEY_IS_SHARE_ENABLED);

        return create(networkEnum, isShareEnabled);
    }

    @NotNull public SocialSharePreferenceDTO create(SocialNetworkEnum socialNetworkEnum, boolean isShareEnabled)
    {
        SocialSharePreferenceDTO socialSharePreferenceDTO = null;

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
