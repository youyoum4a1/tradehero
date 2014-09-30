package com.tradehero.th.api.users;

import android.content.Context;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.misc.DeviceType;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.persistence.prefs.SavedPushDeviceIdentifier;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.VersionUtils;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class LoginSignUpFormDTO
{
    @JsonIgnore
    private final Map<String, String> tokenFieldMap;

    //region static fields
    public final boolean useOnlyHeroCount;
    public final String deviceToken;
    public final DeviceType clientType;
    public final String clientVersion;
    public final boolean isEmailLogin;
    //endregion

    @JsonAnyGetter
    public Map<String, String> getTokenFieldMap()
    {
        return tokenFieldMap;
    }

    public LoginSignUpFormDTO(
            Map<String, String> tokenFieldMap,
            boolean useOnlyHeroCount,
            String deviceToken,
            DeviceType clientType,
            String clientVersion,
            boolean isEmailLogin)
    {
        this.tokenFieldMap = tokenFieldMap;
        this.useOnlyHeroCount = useOnlyHeroCount;
        this.deviceToken = deviceToken;
        this.clientType = clientType;
        this.clientVersion = clientVersion;
        this.isEmailLogin = isEmailLogin;
    }

    public static class Builder
    {
        private final StringPreference savedPushIdentifier;
        private final String versionId;
        private final Map<String, String> fieldMaps;
        private final boolean useOnlyHeroCount;
        private SocialNetworkEnum socialNetworkEnum;
        private String accessToken;
        private String accessTokenSecret;
        private boolean isEmailLogin;

        @Inject public Builder(Context context, @SavedPushDeviceIdentifier StringPreference savedPushIdentifier)
        {
            this.fieldMaps = new HashMap<>();
            this.versionId = VersionUtils.getVersionId(context);
            this.savedPushIdentifier = savedPushIdentifier;
            // TODO what is this?
            this.useOnlyHeroCount = false;
        }

        public Builder type(SocialNetworkEnum socialNetworkEnum)
        {
            this.socialNetworkEnum = socialNetworkEnum;
            if (socialNetworkEnum == SocialNetworkEnum.TH)
            {
                isEmailLogin = true;
            }
            return this;
        }

        public Builder accessToken(String accessToken)
        {
            this.accessToken = accessToken;
            return this;
        }

        public Builder tokenSecret(String accessTokenSecret)
        {
            this.accessTokenSecret = accessTokenSecret;
            return this;
        }

        public LoginSignUpFormDTO build()
        {
            if (socialNetworkEnum.getAccessTokenName() != null)
            {
                fieldMaps.put(socialNetworkEnum.getAccessTokenName(), accessToken);
            }
            if (socialNetworkEnum.getAccessTokenSecretName() != null)
            {
                fieldMaps.put(socialNetworkEnum.getAccessTokenSecretName(), accessTokenSecret);
            }
            return new LoginSignUpFormDTO(fieldMaps, useOnlyHeroCount, savedPushIdentifier.get(), Constants.DEVICE_TYPE, versionId, isEmailLogin);
        }
    }
}
