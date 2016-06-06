package com.androidth.general.api.users;

import android.content.Context;
import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.common.persistence.prefs.StringPreference;
import com.androidth.general.api.misc.DeviceType;
import com.androidth.general.auth.AuthData;
import com.androidth.general.persistence.prefs.SavedPushDeviceIdentifier;
import com.androidth.general.utils.Constants;
import com.androidth.general.utils.VersionUtils;
import java.util.Map;
import javax.inject.Inject;

public class LoginSignUpFormDTO
{
    @JsonIgnore
    @NonNull public final AuthData authData;
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
            @NonNull AuthData authData,
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
        @NonNull protected final StringPreference savedPushIdentifier;
        protected final String versionId;
        protected final boolean useOnlyHeroCount;
        protected AuthData authData;
        protected String email;

        @NonNull protected abstract T self();

        public Builder(@NonNull Context context, @NonNull StringPreference savedPushIdentifier)
        {
            this.versionId = VersionUtils.getVersionId(context);
            this.savedPushIdentifier = savedPushIdentifier;
            // TODO what is this?
            this.useOnlyHeroCount = false;
        }

        @NonNull public T authData(@NonNull AuthData authData)
        {
            this.authData = authData;
            return self();
        }

        public T email(String email)
        {
            this.email = email;
            return self();
        }

        @NonNull public LoginSignUpFormDTO build()
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
        @Inject public Builder2(@NonNull Context context, @SavedPushDeviceIdentifier StringPreference savedPushIdentifier)
        {
            super(context, savedPushIdentifier);
        }

        @Override @NonNull protected Builder2 self()
        {
            return this;
        }
    }
}
