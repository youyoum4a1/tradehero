package com.tradehero.th.base;

import android.content.SharedPreferences;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.THAuthenticationProvider;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.UserService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.ConversionException;

/** Created with IntelliJ IDEA. User: tho Date: 8/14/13 Time: 6:15 PM To change this template use File | Settings | File Templates. */
public class THUser
{
    private static final String TAG = THUser.class.getName();
    private static final String PREF_MY_USER = "PREF_MY_USER";
    private static final String PREF_MY_TOKEN = "PREF_MY_TOKEN";
    private static Map<String, THAuthenticationProvider> authenticationProviders = new HashMap<>();

    private static String currentAuthenticationHeader = null;
    private static final UserService service;

    static
    {
        service = NetworkEngine.createService(UserService.class);
    }

    private static HashMap<String, JSONObject> credentials;

    public static String getSessionToken()
    {
        return null;
    }

    public static boolean hasSessionToken()
    {
        return false;
    }

    public static void logInWithAsync(String authType, LogInCallback callback)
    {
        if (!authenticationProviders.containsKey(authType))
        {
            throw new IllegalArgumentException("No authentication provider could be found for the provided authType");
        }
        logInWithAsync(authenticationProviders.get(authType), callback);
    }

    private static void logInWithAsync(final THAuthenticationProvider authenticator, final LogInCallback callback)
    {
        JSONObject savedTokens = credentials.get(authenticator.getAuthType());
        if (savedTokens != null)
        {
            callback.onStart();
            authenticator.restoreAuthentication(savedTokens);
            currentAuthenticationHeader = authenticator.getAuthHeader();
            logInAsyncWithJson(savedTokens, callback);
            return;
        }
        authenticator.authenticate(new THAuthenticationProvider.THAuthenticationCallback()
        {
            @Override public void onStart()
            {
                callback.onStart();
            }

            @Override public void onSuccess(JSONObject json)
            {
                try
                {
                    json.put("type", authenticator.getAuthType());
                }
                catch (JSONException ex)
                {
                }
                saveCredentialsToUserDefaults(json);
                if (callback.onSocialAuthDone(json))
                {
                    currentAuthenticationHeader = authenticator.getAuthHeader();
                    logInAsyncWithJson(json, callback);
                }
            }

            @Override public void onCancel()
            {
                callback.done(null, new THException("Canceled by User"));
            }

            @Override public void onError(Throwable throwable)
            {
                callback.done(null, new THException(throwable));
            }
        });
    }

    public static void logInAsyncWithJson(JSONObject json, final LogInCallback callback)
    {
        service.authenticate(new UserFormDTO(json), new Callback<UserProfileDTO>()
        {
            @Override
            public void success(UserProfileDTO userDTO, Response response)
            {
                saveCurrentUser(userDTO);
                callback.done(userDTO, null);
            }

            @Override public void failure(RetrofitError error)
            {
                callback.done(null, new THException(error));
            }
        });
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
        try
        {
            String serializedUser = Application.getPreferences().getString(PREF_MY_USER, null);
            if (serializedUser != null)
            {
                UserProfileDTO user = (UserProfileDTO) THJsonAdapter.getInstance()
                        .fromBody(serializedUser, UserProfileDTO.class);
                return user;
            }
        }
        catch (ConversionException ex)
        {
            THLog.d(TAG, "User is not in cache!");
        }
        return null;
    }

    public static void registerAuthenticationProvider(THAuthenticationProvider provider)
    {
        authenticationProviders.put(provider.getAuthType(), provider);
    }

    public static void initialize()
    {
        credentials = new HashMap<>();
        loadCredentialsToUserDefaults();
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
            credentials.put(json.getString("type"), json);
        }
        catch (JSONException ex)
        {
            THLog.e(TAG, String.format("JSON (%s) does not has type", json.toString()), ex);
        }

        Set<String> toSave = new HashSet<>();
        for (Map.Entry<String, JSONObject> entry : credentials.entrySet())
        {
            toSave.add(entry.getValue().toString());
        }

        SharedPreferences.Editor prefEditor = Application.getPreferences().edit();
        prefEditor.putStringSet(PREF_MY_TOKEN, toSave);
        prefEditor.commit();
    }

    private static void loadCredentialsToUserDefaults()
    {
        Set<String> savedTokens = Application.getPreferences().getStringSet(PREF_MY_TOKEN, new HashSet<String>());
        for (String token : savedTokens)
        {
            try
            {
                JSONObject json = new JSONObject(token);
                credentials.put(json.getString("type"), json);
            }
            catch (JSONException e)
            {
                THLog.e(TAG, String.format("Unable to parse [%s] to JSON", token), e);
            }
        }
    }

    public static String getAuthenticationHeader()
    {
        return currentAuthenticationHeader;
    }
}
