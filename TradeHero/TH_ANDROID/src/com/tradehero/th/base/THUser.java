package com.tradehero.th.base;

import android.content.SharedPreferences;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.auth.THAuthenticationProvider;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.misc.exception.THException.ExceptionCode;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.UserService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

/** Created with IntelliJ IDEA. User: tho Date: 8/14/13 Time: 6:15 PM */
public class THUser
{
    private static final String TAG = THUser.class.getName();
    private static final String PREF_MY_USER = "PREF_MY_USER";
    private static final String PREF_MY_TOKEN = "PREF_MY_TOKEN";
    private static final String CURRENT_SESSION_TOKEN = "PREF_CURRENT_SESSION_TOKEN";

    private static AuthenticationMode authenticationMode;
    private static HashMap<String, JSONObject> credentials;
    private static THAuthenticationProvider authenticator;
    private static Map<String, THAuthenticationProvider> authenticationProviders = new HashMap<>();
    private static String currentSessionToken;

    public static void initialize()
    {
        credentials = new HashMap<>();
        loadCredentialsToUserDefaults();
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
                logInAsyncWithJson(savedTokens, callback);
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
        UserFormDTO userFormDTO;
        try
        {
            userFormDTO = UserFormFactory.create(json);
        }
        catch (JSONException e)
        {
            THLog.e("THUser.logInAsyncWithJson", e.getMessage(), e);
            return;
        }
        UserService userService = NetworkEngine.createService(UserService.class);
        userService.authenticate(authenticator.getAuthHeader(),
                authenticationMode.getEndPoint(),
                userFormDTO,
                createCallbackForLogInAsyncWithJson(json, callback));
    }

    private static THCallback<UserProfileDTO> createCallbackForLogInAsyncWithJson (final JSONObject json, final LogInCallback callback)
    {
        return new THCallback<UserProfileDTO>()
        {
            @Override
            public void success(UserProfileDTO userDTO, THResponse response)
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

    private static void saveCurrentUser(UserBaseDTO userDTO)
    {
        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            THJsonAdapter.getInstance().toBody(userDTO).writeTo(byteArrayOutputStream);
            SharedPreferences.Editor pref = Application.getPreferences().edit();
            pref.putString(PREF_MY_USER, byteArrayOutputStream.toString("UTF-8"));
            pref.commit();
        }
        catch (IOException ex)
        {
            THLog.e(TAG, "User data is not saved", ex);
        }
    }

    public static UserProfileDTO getCurrentUser()
    {
        String serializedUser = Application.getPreferences().getString(PREF_MY_USER, null);
        if (serializedUser != null)
        {
            UserProfileDTO user = (UserProfileDTO) THJsonAdapter.getInstance()
                    .fromBody(serializedUser, UserProfileDTO.class);
            return user;
        }
        return null;
    }

    public static void registerAuthenticationProvider(THAuthenticationProvider provider)
    {
        authenticationProviders.put(provider.getAuthType(), provider);
    }

    private static void saveCredentialsToUserDefaults(JSONObject json)
    {
        if (credentials == null)
        {
            return;
        }

        THLog.d(TAG, String.format("%d authentication tokens loaded", credentials.size()));

        try
        {
            THAuthenticationProvider currentProvider = authenticationProviders.get(json.get(UserFormFactory.KEY_TYPE));

            currentSessionToken = currentProvider.getAuthHeaderParameter();

            credentials.put(json.getString(UserFormFactory.KEY_TYPE), json);

        }
        catch (JSONException ex)
        {
            THLog.e(TAG, String.format("JSON (%s) does not have type", json.toString()), ex);
        }

        Set<String> toSave = new HashSet<>();
        for (Map.Entry<String, JSONObject> entry : credentials.entrySet())
        {
            toSave.add(entry.getValue().toString());
        }

        SharedPreferences.Editor prefEditor = Application.getPreferences().edit();
        prefEditor.putStringSet(PREF_MY_TOKEN, toSave);
        prefEditor.putString(CURRENT_SESSION_TOKEN, currentSessionToken);
        prefEditor.commit();
    }

    private static void loadCredentialsToUserDefaults()
    {
        Set<String> savedTokens = Application.getPreferences().getStringSet(PREF_MY_TOKEN, new HashSet<String>());
        currentSessionToken = Application.getPreferences().getString(CURRENT_SESSION_TOKEN, null);
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
    }

    public static void setAuthenticationMode(AuthenticationMode authenticationMode)
    {
        THUser.authenticationMode = authenticationMode;
    }
}
