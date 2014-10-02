package com.tradehero.th.api.users;

import android.content.Context;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.misc.DeviceType;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.persistence.prefs.SavedPushDeviceIdentifier;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.VersionUtils;
import java.util.Map;
import javax.inject.Inject;

public class LoginSignUpFormDTO
{
    @JsonIgnore
    public final AuthData authData;
    //region static fields
    public final boolean useOnlyHeroCount;
    public final String deviceToken;
    public final DeviceType clientType;
    public final String clientVersion;
    //endregion

    @JsonAnyGetter
    public Map<String, String> getTokenFieldMap()
    {
        return authData.getTokenMap();
    }

    public final boolean isEmailLogin()
    {
        return authData.socialNetworkEnum == SocialNetworkEnum.TH;
    }

    public LoginSignUpFormDTO(
            AuthData authData,
            boolean useOnlyHeroCount,
            String deviceToken,
            DeviceType clientType,
            String clientVersion)
    {
        this.authData = authData;
        this.useOnlyHeroCount = useOnlyHeroCount;
        this.deviceToken = deviceToken;
        this.clientType = clientType;
        this.clientVersion = clientVersion;
    }

    public static class Builder
    {
        private final StringPreference savedPushIdentifier;
        private final String versionId;
        private final boolean useOnlyHeroCount;
        private AuthData authData;

        @Inject public Builder(Context context, @SavedPushDeviceIdentifier StringPreference savedPushIdentifier)
        {
            this.versionId = VersionUtils.getVersionId(context);
            this.savedPushIdentifier = savedPushIdentifier;
            // TODO what is this?
            this.useOnlyHeroCount = false;
        }

        public Builder authData(AuthData authData)
        {
            this.authData = authData;
            return this;
        }

        public LoginSignUpFormDTO build()
        {
            ensureSaneDefaults();
            return new LoginSignUpFormDTO(authData, useOnlyHeroCount, savedPushIdentifier.get(), Constants.DEVICE_TYPE, versionId);
        }

        private void ensureSaneDefaults()
        {
            if (authData == null)
            {
                throw new IllegalStateException("authData should not be null");
            }
        }
    }
}
