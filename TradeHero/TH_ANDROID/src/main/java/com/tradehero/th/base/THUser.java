package com.tradehero.th.base;

import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.misc.DeviceType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.LoginFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.auth.THAuthenticationProvider;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.misc.exception.THException.ExceptionCode;
import com.tradehero.th.network.service.SessionService;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.prefs.AuthenticationType;
import com.tradehero.th.persistence.prefs.SavedCredentials;
import com.tradehero.th.persistence.prefs.SessionToken;
import com.tradehero.th.persistence.social.VisitedFriendListPrefs;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.Constants;
import com.urbanairship.push.PushManager;
import dagger.Lazy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: tho Date: 8/14/13 Time: 6:15 PM */
public class THUser
{
    private static AuthenticationMode authenticationMode;
    private static THAuthenticationProvider authenticator;
    private static Map<String, THAuthenticationProvider> authenticationProviders = new HashMap<>();

    private static HashMap<String, JSONObject> credentials;

    @Inject @SessionToken static StringPreference currentSessionToken;
    @Inject @AuthenticationType public /* TODO, remove public */ static StringPreference currentAuthenticationType;
    @Inject @SavedCredentials static StringSetPreference savedCredentials;
    @Inject static CurrentUserId currentUserId;

    @Inject static Lazy<SharedPreferences> sharedPreferences;
    @Inject static Lazy<UserService> userService;
    @Inject static Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject static Lazy<SessionService> sessionService;
    @Inject static Lazy<UserProfileCache> userProfileCache;
    @Inject static Lazy<DTOCacheUtil> dtoCacheUtil;

    public static void initialize()
    {
        credentials = new HashMap<>();
        loadCredentialsToUserDefaults();
    }

    private static void loadCredentialsToUserDefaults()
    {
        for (String token : savedCredentials.get())
        {
            try
            {
                JSONObject json = new JSONObject(token);
                credentials.put(json.getString(UserFormFactory.KEY_TYPE), json);
            }
            catch (JSONException e)
            {
                Timber.e("Unable to parse [%s] to JSON", token, e);
            }
        }
    }

    public static void logInWithAsync(String authType, LogInCallback callback)
    {
        if (!authenticationProviders.containsKey(authType))
        {
            throw new IllegalArgumentException("No authentication provider could be found for the provided authType");
        }
        authenticator = authenticationProviders.get(authType);
        logInWithAsync(authenticationProviders.get(authType), callback);
    }

    private static void logInWithAsync(final THAuthenticationProvider authenticator, final LogInCallback callback)
    {
        JSONObject savedTokens = credentials.get(authenticator.getAuthType());
        if (savedTokens != null)
        {
            callback.onStart();
            if (authenticator.restoreAuthentication(savedTokens))
            {
                if (callback.onSocialAuthDone(savedTokens))
                {
                    logInAsyncWithJson(savedTokens, callback);
                }
                return;
            }
        }
        THAuthenticationProvider.THAuthenticationCallback outerCallback = createCallbackForLogInWithAsync (callback);
        authenticator.authenticate(outerCallback);
    }

    public static void logInAsyncWithJson(final JSONObject json, final LogInCallback callback)
    {
        UserFormDTO userFormDTO = UserFormFactory.create(json);
        if (userFormDTO == null)
        {
            // input error, unable to parse as json data
            return;
        }
        Timber.d("APID: %s,authenticationMode :%s", PushManager.shared().getAPID(),authenticationMode);
        userFormDTO.deviceToken = PushManager.shared().getAPID();

        if (authenticationMode == null)
        {
            authenticationMode = AuthenticationMode.SignIn;
        }

        switch (authenticationMode)
        {
            case SignUpWithEmail:
                Timber.d("SignUpWithEmail Auth Header "+authenticator.getAuthHeader());
                userServiceWrapper.get().signUpWithEmail(
                        authenticator.getAuthHeader(),
                        userFormDTO,
                        createCallbackForSignUpAsyncWithJson(json, callback));
                break;
            case SignUp:
                Timber.d("SignUp Auth Header "+authenticator.getAuthHeader());
                userService.get().signUp(
                        authenticator.getAuthHeader(),
                        userFormDTO,
                        createCallbackForSignUpAsyncWithJson(json, callback));
                break;
            case SignIn:
                LoginFormDTO loginFormDTO = new LoginFormDTO(PushManager.shared().getAPID(), DeviceType.Android, Constants.TH_CLIENT_VERSION_VALUE);
                Timber.d("login Auth Header "+authenticator.getAuthHeader());
                sessionService.get().login(authenticator.getAuthHeader(), loginFormDTO, createCallbackForSignInAsyncWithJson(json, callback));
                break;
        }
    }


    private static THAuthenticationProvider.THAuthenticationCallback createCallbackForLogInWithAsync (final LogInCallback callback)
    {
        return new THAuthenticationProvider.THAuthenticationCallback()
        {
            @Override public void onStart()
            {
                callback.onStart();
            }

            @Override public void onSuccess(JSONObject json)
            {
                try
                {
                    json.put(UserFormFactory.KEY_TYPE, authenticator.getAuthType());
                }
                catch (JSONException ex)
                {
                }
                if (callback.onSocialAuthDone(json))
                {
                    logInAsyncWithJson(json, callback);
                }
            }

            @Override public void onCancel()
            {
                callback.done(null, ExceptionCode.UserCanceled.toException());
            }

            @Override public void onError(Throwable throwable)
            {
                callback.done(null, new THException(throwable));
            }
        };
    }

    private static THCallback<UserProfileDTO> createCallbackForSignUpAsyncWithJson(final JSONObject json, final LogInCallback callback)
    {
        return new THCallback<UserProfileDTO>()
        {
            @Override public void success(UserProfileDTO userDTO, THResponse response)
            {
                currentUserId.set(userDTO.id);
                saveCredentialsToUserDefaults(json);
                callback.done(userDTO, null);
            }

            @Override public void failure(THException error)
            {
                callback.done(null, error);
            }
        };
    }

    private static THCallback<UserLoginDTO> createCallbackForSignInAsyncWithJson(final JSONObject json, final LogInCallback callback)
    {
        return new THCallback<UserLoginDTO>()
        {
            @Override public void success(UserLoginDTO userLoginDTO, THResponse response)
            {
                UserProfileDTO userProfileDTO = userLoginDTO.profileDTO;
                userProfileCache.get().put(userProfileDTO.getBaseKey(), userProfileDTO);
                currentUserId.set(userProfileDTO.id);
                saveCredentialsToUserDefaults(json);
                callback.done(userProfileDTO, null);
            }

            @Override public void failure(THException error)
            {
                callback.done(null, error);
            }
        };
    }

    public static void registerAuthenticationProvider(THAuthenticationProvider provider)
    {
        authenticationProviders.put(provider.getAuthType(), provider);
    }

    /**
     * @param json json data is from social media
     */
    public static void saveCredentialsToUserDefaults(JSONObject json)
    {
        if (credentials == null)
        {
            Timber.d("saveCredentialsToUserDefaults: Credentials were null");
            return;
        }

        Timber.d("%d authentication tokens loaded", credentials.size());

        try
        {
            currentAuthenticationType.set(json.getString(UserFormFactory.KEY_TYPE));
            credentials.put(currentAuthenticationType.get(), json);
        }
        catch (JSONException ex)
        {
            Timber.e("JSON (%s) does not have type", json.toString(), ex);
            return;
        }

        Set<String> toSave = new HashSet<>();
        for (JSONObject entry : credentials.values())
        {
            toSave.add(entry.toString());
        }
        savedCredentials.set(toSave);

        THAuthenticationProvider currentProvider = authenticationProviders.get(currentAuthenticationType.get());
        currentSessionToken.set(currentProvider.getAuthHeaderParameter());
    }

    public static void clearCurrentUser()
    {
        currentSessionToken.delete();
        userProfileCache.get().invalidate(currentUserId.toUserBaseKey());
        dtoCacheUtil.get().clearUserRelatedCaches();
        currentUserId.delete();
        credentials.clear();
        VisitedFriendListPrefs.clearVisitedIdList();

        THAuthenticationProvider currentProvider = authenticationProviders.get(currentAuthenticationType.get());
        if (currentProvider != null)
        {
            currentProvider.deauthenticate();
        }

        // clear all preferences
        SharedPreferences.Editor prefEditor = sharedPreferences.get().edit();
        prefEditor.clear();
        prefEditor.commit();
    }

    public static void setAuthenticationMode(AuthenticationMode authenticationMode)
    {
        THUser.authenticationMode = authenticationMode;
    }

    public static JSONObject currentCredentials()
    {
        return credentials.get(currentAuthenticationType.get());
    }

    public static String getAuthHeader()
    {
        return currentAuthenticationType.get() + " " + currentSessionToken.get();
    }

    public static void removeCredential(String authenticationHeader)
    {
        if (credentials == null)
        {
            Timber.d("saveCredentialsToUserDefaults: Credentials were null");
            return;
        }

        Timber.d("%d authentication tokens loaded", credentials.size());

        credentials.remove(authenticationHeader);
        savedCredentials.delete();
    }
}
