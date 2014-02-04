package com.tradehero.th.base;

import android.content.SharedPreferences;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.misc.DeviceType;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.LoginFormDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
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
import com.tradehero.th.persistence.social.VisitedFriendListPrefs;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.Constants;
import com.urbanairship.push.PushManager;
import dagger.Lazy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 8/14/13 Time: 6:15 PM */
public class THUser
{
    private static final String TAG = THUser.class.getSimpleName();

    public static final String PREF_CURRENT_USER_KEY = THUser.class.getName() + ".PREF_CURRENT_USER_KEY";
    public static final String PREF_MY_USER = THUser.class.getName() + ".PREF_MY_USER";
    private static final String PREF_MY_TOKEN = THUser.class.getName() + ".PREF_MY_TOKEN";
    private static final String PREF_CURRENT_SESSION_TOKEN = THUser.class.getName() +  ".PREF_CURRENT_SESSION_TOKEN";
    private static final String PREF_CURRENT_AUTHENTICATION_TYPE = THUser.class.getName() + ".PREF_AUTHENTICATION_TYPE";

    private static AuthenticationMode authenticationMode;
    private static HashMap<String, JSONObject> credentials;
    private static THAuthenticationProvider authenticator;
    private static Map<String, THAuthenticationProvider> authenticationProviders = new HashMap<>();
    private static String currentSessionToken;
    private static String currentAuthenticationType;

    @Inject static Lazy<SharedPreferences> sharedPreferences;
    @Inject static Lazy<UserService> userService;
    @Inject static Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject static Lazy<SessionService> sessionService;
    @Inject static protected Lazy<UserProfileCache> userProfileCache;
    @Inject static protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    @Inject static protected Lazy<DTOCacheUtil> dtoCacheUtil;

    public static void initialize()
    {
        credentials = new HashMap<>();
        loadCredentialsToUserDefaults();
    }

    private static void loadCredentialsToUserDefaults()
    {
        collectCurrentUserBaseKeyFromPref();
        Set<String> savedTokens = sharedPreferences.get().getStringSet(PREF_MY_TOKEN, new HashSet<String>());
        currentSessionToken = sharedPreferences.get().getString(PREF_CURRENT_SESSION_TOKEN, null);
        currentAuthenticationType = sharedPreferences.get().getString(PREF_CURRENT_AUTHENTICATION_TYPE, null);
        for (String token : savedTokens)
        {
            try
            {
                JSONObject json = new JSONObject(token);
                credentials.put(json.getString(UserFormFactory.KEY_TYPE), json);
            }
            catch (JSONException e)
            {
                THLog.e(TAG, String.format("Unable to parse [%s] to JSON", token), e);
            }
        }
        THLog.d(TAG, "loadCredentialsToUserDefaults: CurrentSessionToken: " + currentSessionToken);
        THLog.d(TAG, "loadCredentialsToUserDefaults: CurrentAuthenticationType: " + currentAuthenticationType);
    }

    public static String getSessionToken()
    {
        return currentSessionToken;
    }

    public static boolean hasSessionToken()
    {
        return getSessionToken() != null;
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

    public static void logInAsyncWithJson(final JSONObject json, final LogInCallback callback)
    {
        UserFormDTO userFormDTO = UserFormFactory.create(json);
        if (userFormDTO == null)
        {
            // input error, unable to parse as json data
            return;
        }

        if (authenticationMode == null)
        {
            authenticationMode = AuthenticationMode.SignIn;
        }

        switch (authenticationMode)
        {
            case SignUpWithEmail:
                // TODO I love this smell of hacking :v
                userServiceWrapper.get().signUpWithEmail(
                        authenticator.getAuthHeader(),
                        userFormDTO,
                        createCallbackForSignUpAsyncWithJson(json, callback));
                break;
            case SignUp:
                userService.get().signUp(
                        authenticator.getAuthHeader(),
                        userFormDTO,
                        createCallbackForSignUpAsyncWithJson(json, callback));
                break;
            case SignIn:
                LoginFormDTO loginFormDTO = new LoginFormDTO(PushManager.shared().getAPID(), DeviceType.Android, Constants.TH_CLIENT_VERSION_VALUE);
                sessionService.get().login(authenticator.getAuthHeader(), loginFormDTO, createCallbackForSignInAsyncWithJson(json, callback));
                break;
        }
    }

    private static THCallback<UserProfileDTO> createCallbackForSignUpAsyncWithJson(final JSONObject json, final LogInCallback callback)
    {
        return new THCallback<UserProfileDTO>()
        {
            @Override public void success(UserProfileDTO userDTO, THResponse response)
            {
                saveCurrentUser(userDTO);
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
                saveCurrentUser(userProfileDTO);
                saveCredentialsToUserDefaults(json);
                callback.done(userProfileDTO, null);
            }

            @Override public void failure(THException error)
            {
                callback.done(null, error);
            }
        };
    }

    private static void saveCurrentUser(UserBaseDTO userDTO)
    {
        try
        {
            saveCurrentUserBaseKey(userDTO.getBaseKey());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            THJsonAdapter.getInstance().toBody(userDTO).writeTo(byteArrayOutputStream);
            SharedPreferences.Editor pref = sharedPreferences.get().edit();
            pref.putString(PREF_MY_USER, byteArrayOutputStream.toString("UTF-8"));
            pref.commit();
        }
        catch (IOException ex)
        {
            THLog.e(TAG, "User data is not saved", ex);
        }
    }

    public static UserBaseDTO getCurrentUserBase()
    {
        String serializedUser = sharedPreferences.get().getString(PREF_MY_USER, null);
        if (serializedUser != null)
        {
            UserBaseDTO user = (UserBaseDTO) THJsonAdapter.getInstance()
                    .fromBody(serializedUser, UserBaseDTO.class);
            return user;
        }
        return null;
    }

    private static void saveCurrentUserBaseKey(UserBaseKey userBaseKey)
    {
        SharedPreferences.Editor pref = sharedPreferences.get().edit();
        pref.putInt(PREF_CURRENT_USER_KEY, userBaseKey.key);
        pref.commit();
        currentUserBaseKeyHolder.setCurrentUserBaseKey(userBaseKey);
    }

    public static void collectCurrentUserBaseKeyFromPref()
    {
        int key = sharedPreferences.get().getInt(PREF_CURRENT_USER_KEY, 0);
        UserBaseKey userBaseKey = new UserBaseKey(key);
        currentUserBaseKeyHolder.setCurrentUserBaseKey(userBaseKey);
    }

    public static void registerAuthenticationProvider(THAuthenticationProvider provider)
    {
        authenticationProviders.put(provider.getAuthType(), provider);
    }

    public static void saveCredentialsToUserDefaults(JSONObject json)
    {
        if (credentials == null)
        {
            THLog.d(TAG, "saveCredentialsToUserDefaults: Credentials were null");
            return;
        }

        THLog.d(TAG, String.format("%d authentication tokens loaded", credentials.size()));

        try
        {
            currentAuthenticationType = json.getString(UserFormFactory.KEY_TYPE);
            credentials.put(currentAuthenticationType, json);
        }
        catch (JSONException ex)
        {
            THLog.e(TAG, String.format("JSON (%s) does not have type", json.toString()), ex);
            return;
        }

        Set<String> toSave = new HashSet<>();
        for (JSONObject entry : credentials.values())
        {
            toSave.add(entry.toString());
        }

        THAuthenticationProvider currentProvider = authenticationProviders.get(currentAuthenticationType);
        currentSessionToken = currentProvider.getAuthHeaderParameter();

        SharedPreferences.Editor prefEditor = sharedPreferences.get().edit();
        prefEditor.putStringSet(PREF_MY_TOKEN, toSave);
        prefEditor.putString(PREF_CURRENT_SESSION_TOKEN, currentSessionToken);
        prefEditor.putString(PREF_CURRENT_AUTHENTICATION_TYPE, currentAuthenticationType);
        prefEditor.commit();
    }

    public static void clearCurrentUser()
    {
        currentSessionToken = null;
        userProfileCache.get().invalidate(currentUserBaseKeyHolder.getCurrentUserBaseKey());
        dtoCacheUtil.get().clearUserRelatedCaches();
        currentUserBaseKeyHolder.setCurrentUserBaseKey(new UserBaseKey(0));
        credentials.clear();
        VisitedFriendListPrefs.clearVisitedIdList();

        // clear all preferences
        SharedPreferences.Editor prefEditor = sharedPreferences.get().edit();
        prefEditor.clear();
        prefEditor.commit();

        THAuthenticationProvider currentProvider = authenticationProviders.get(currentAuthenticationType);
        if (currentProvider != null)
        {
            currentProvider.deauthenticate();
        }
    }

    public static void setAuthenticationMode(AuthenticationMode authenticationMode)
    {
        THUser.authenticationMode = authenticationMode;
    }

    public static JSONObject currentCredentials()
    {
        return credentials.get(currentAuthenticationType);
    }

    public static String getAuthHeader()
    {
        return currentAuthenticationType + " " + currentSessionToken;
    }

    // whether update is posted
    public static boolean updateProfile(JSONObject userFormJSON, final LogInCallback callback)
    {
        UserFormDTO userFormDTO = UserFormFactory.create(userFormJSON);
        if (userFormDTO == null)
        {
            return false;
        }

        userServiceWrapper.get().updateProfile(
                currentUserBaseKeyHolder.getCurrentUserBaseKey(),
                userFormDTO,
                createCallbackForSignUpAsyncWithJson(userFormJSON, callback));
        return true;
    }

    public static String getCurrentAuthenticationType()
    {
        return currentAuthenticationType;
    }

    public static void removeCredential(String authenticationHeader)
    {
        if (credentials == null)
        {
            THLog.d(TAG, "saveCredentialsToUserDefaults: Credentials were null");
            return;
        }

        THLog.d(TAG, String.format("%d authentication tokens loaded", credentials.size()));

        credentials.remove(authenticationHeader);

        Set<String> toSave = new HashSet<>();
        for (JSONObject entry : credentials.values())
        {
            toSave.add(entry.toString());
        }

        SharedPreferences.Editor prefEditor = sharedPreferences.get().edit();
        prefEditor.putStringSet(PREF_MY_TOKEN, toSave);
        prefEditor.commit();
    }
}
