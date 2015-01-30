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
    public final String email;
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

    public LoginSignUpFormDTO(
            AuthData authData,
            String email,
            boolean useOnlyHeroCount,
            String deviceToken,
            DeviceType clientType,
            String clientVersion)
    {
        this.authData = authData;
        this.email = email;
        this.useOnlyHeroCount = useOnlyHeroCount;
        this.deviceToken = deviceToken;
        this.clientType = clientType;
        this.clientVersion = clientVersion;
    }

    protected abstract static class Builder<T extends Builder<T>>
    {
        protected final StringPreference savedPushIdentifier;
        protected final String versionId;
        protected final boolean useOnlyHeroCount;
        protected AuthData authData;
        protected String email;

        protected abstract T self();

        public Builder(Context context, StringPreference savedPushIdentifier)
        {
            this.versionId = VersionUtils.getVersionId(context);
            this.savedPushIdentifier = savedPushIdentifier;
            // TODO what is this?
            this.useOnlyHeroCount = false;
        }

        public T authData(AuthData authData)
        {
            this.authData = authData;
            return self();
        }

        public T email(String email)
        {
            this.email = email;
            return self();
        }

        public LoginSignUpFormDTO build()
        {
            ensureSaneDefaults();
            return new LoginSignUpFormDTO(authData, email, useOnlyHeroCount, savedPushIdentifier.get(), Constants.DEVICE_TYPE, versionId);
        }

        private void ensureSaneDefaults()
        {
            if (authData == null)
            {
                throw new IllegalStateException("authData should not be null");
            }

            if (email == null)
            {
                email = authData.email;
            }
        }
    }

    public static class Builder2 extends Builder<Builder2>
    {
        @Inject public Builder2(Context context, @SavedPushDeviceIdentifier StringPreference savedPushIdentifier)
        {
            super(context, savedPushIdentifier);
        }

        @Override protected Builder2 self()
        {
            return this;
        }
    }
}
