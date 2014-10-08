package com.tradehero.th.api.form;

import android.content.Context;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.api.misc.DeviceType;
import com.tradehero.th.api.users.LoginSignUpFormDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.models.graphics.BitmapTypedOutput;
import com.tradehero.th.persistence.prefs.SavedPushDeviceIdentifier;
import com.tradehero.th.utils.Constants;
import javax.inject.Inject;
import retrofit.mime.TypedOutput;

public class UserFormDTO extends LoginSignUpFormDTO
{
    public static final String KEY_TYPE = "type";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PASSWORD_CONFIRM = "confirmPassword";
    public static final String KEY_DISPLAY_NAME = "displayName";
    public static final String KEY_INVITE_CODE = "inviteCode";
    public static final String KEY_FIRST_NAME = "firstName";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_EMAIL_NOTIFICATION_ENABLED = "emailNotificationsEnabled";
    public static final String KEY_PUSH_NOTIFICATION_ENABLED = "pushNotificationsEnabled";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";

    public final String username;
    public final String password;
    public final String passwordConfirmation;
    public final String firstName;
    public final String lastName;
    public final String displayName;
    public final String inviteCode;

    //notifications settings
    public Boolean pushNotificationsEnabled;
    public Boolean emailNotificationsEnabled;

    // optional
    public String biography;
    public String location;
    public String website;
    public String deviceToken;

    public TypedOutput profilePicture;

    public UserFormDTO(AuthData authData, String email, boolean useOnlyHeroCount, String deviceToken,
            DeviceType clientType, String clientVersion, String username, String password, String passwordConfirmation, String firstName,
            String lastName, String displayName, String inviteCode)
    {
        super(authData, email, useOnlyHeroCount, deviceToken, clientType, clientVersion);
        this.username = username;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.inviteCode = inviteCode;
    }

    @Override public String toString()
    {
        return "UserFormDTO{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", passwordConfirmation='" + passwordConfirmation + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", inviteCode='" + inviteCode + '\'' +
                ", pushNotificationsEnabled=" + pushNotificationsEnabled +
                ", emailNotificationsEnabled=" + emailNotificationsEnabled +
                ", biography='" + biography + '\'' +
                ", location='" + location + '\'' +
                ", website='" + website + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                '}';
    }

    public static abstract class Builder<T extends Builder<T>> extends LoginSignUpFormDTO.Builder<T>
    {
        private String password;
        private boolean pushNotificationsEnabled;
        private Boolean emailNotificationsEnabled;
        private String displayName;
        private String firstName;
        private String lastName;
        private BitmapTypedOutput profilePicture;
        private String inviteCode;

        public Builder(Context context, @SavedPushDeviceIdentifier StringPreference savedPushIdentifier)
        {
            super(context, savedPushIdentifier);
        }

        public UserFormDTO build()
        {
            return new UserFormDTO(authData, email, useOnlyHeroCount, savedPushIdentifier.get(), Constants.DEVICE_TYPE, versionId, displayName, password,
                    password, firstName, lastName, displayName, inviteCode);
        }

        public T password(String password)
        {
            this.password = password;
            return self();
        }

        public T pushNotificationsEnabled(boolean pushNotificationsEnabled)
        {
            this.pushNotificationsEnabled = pushNotificationsEnabled;
            return self();
        }

        public T emailNotificationsEnabled(Boolean emailNotificationsEnabled)
        {
            this.emailNotificationsEnabled = emailNotificationsEnabled;
            return self();
        }

        public T displayName(String displayName)
        {
            this.displayName = displayName;
            return self();
        }

        public T firstName(String firstName)
        {
            this.firstName = firstName;
            return self();
        }

        public T lastName(String lastName)
        {
            this.lastName = lastName;
            return self();
        }

        public T profilePicture(BitmapTypedOutput profilePicture)
        {
            this.profilePicture = profilePicture;
            return self();
        }

        public T inviteCode(String inviteCode)
        {
            this.inviteCode = inviteCode;
            return self();
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
