package com.tradehero.th.base;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.AuthenticationActivity;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.activities.GuideActivity;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.LoginSignUpFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.signup.LoginSignUpFormDTOFactory;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.auth.THAuthenticationProvider;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.misc.exception.THException.ExceptionCode;
import com.tradehero.th.models.user.auth.CredentialsDTO;
import com.tradehero.th.models.user.auth.CredentialsDTOFactory;
import com.tradehero.th.models.user.auth.CredentialsSetPreference;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.prefs.BindGuestUser;
import com.tradehero.th.persistence.prefs.DiviceID;
import com.tradehero.th.persistence.social.VisitedFriendListPrefs;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import org.json.JSONException;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class THUser
{
    private static AuthenticationMode authenticationMode;
    private static THAuthenticationProvider authenticator;
    private static final Map<String, THAuthenticationProvider> authenticationProviders = new HashMap<>();

    private static HashMap<String, CredentialsDTO> typedCredentials;

    @Inject static MainCredentialsPreference mainCredentialsPreference;
    @Inject static CredentialsSetPreference credentialsSetPreference;
    @Inject static CurrentUserId currentUserId;

    @Inject @ForUser static Lazy<SharedPreferences> sharedPreferences;
    @Inject static Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject static Lazy<SessionServiceWrapper> sessionServiceWrapper;
    @Inject static Lazy<UserProfileCache> userProfileCache;
    @Inject static Lazy<DTOCacheUtil> dtoCacheUtil;
    @Inject static Lazy<CurrentActivityHolder> currentActivityHolder;
    @Inject static CredentialsDTOFactory credentialsDTOFactory;
    @Inject static LoginSignUpFormDTOFactory loginSignUpFormDTOFactory;
    @Inject @BindGuestUser static BooleanPreference mBindGuestUserPreference;
    @Inject @DiviceID static StringPreference mDeviceIDStringPreference;

    public static void initialize()
    {
        typedCredentials = new HashMap<>();
        for (CredentialsDTO credentialsDTO : credentialsSetPreference.getCredentials())
        {
            typedCredentials.put(credentialsDTO.getAuthType(), credentialsDTO);
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
        CredentialsDTO savedCredentials = typedCredentials.get(authenticator.getAuthType());
        if (savedCredentials != null)
        {
            callback.onStart();
            JSONCredentials jsonCredentials = null;
            try
            {
                jsonCredentials = savedCredentials.createJSON();
                if (authenticator.restoreAuthentication(jsonCredentials))
                {
                    if (callback.onSocialAuthDone(jsonCredentials))
                    {
                        logInAsyncWithJson(savedCredentials, callback);
                    }
                    return;
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        THAuthenticationProvider.THAuthenticationCallback outerCallback = createCallbackForLogInWithAsync (callback);
        authenticator.authenticate(outerCallback);
    }

    public static void logInAsyncWithJson(final CredentialsDTO credentialsDTO, final LogInCallback callback)
    {
        UserFormDTO userFormDTO = credentialsDTO.createUserFormDTO();
        if (userFormDTO == null)
        {
            // input error, unable to parse as json data
            THToast.show(R.string.authentication_error_creating_signup_form);
            return;
        }

        if (authenticationMode == null)
        {
            authenticationMode = AuthenticationMode.SignIn;
        }

        switch (authenticationMode)
        {
            case SignUpWithEmail:
                if (mBindGuestUserPreference.get())
                {
                    userFormDTO.deviceAccessToken = getIMEI();
                }
                userServiceWrapper.get().signUpWithEmail(authenticator.getAuthHeader(), userFormDTO,
                        createCallbackForSignUpAsyncWithJson(credentialsDTO, callback));
                break;
            case SignUp:
                userServiceWrapper.get().signUp(authenticator.getAuthHeader(), userFormDTO,
                        createCallbackForSignUpAsyncWithJson(credentialsDTO, callback));
                break;
            case SignIn:
            case Device:
                //use new DTO, combine login and social register
                LoginSignUpFormDTO loginSignUpFormDTO = loginSignUpFormDTOFactory.create(userFormDTO);
                sessionServiceWrapper.get().signupAndLogin(authenticator.getAuthHeader(),
                        loginSignUpFormDTO,
                        createCallbackForSignInAsyncWithJson(credentialsDTO, callback));
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

            @Override public void onSuccess(JSONCredentials json)
            {
                try
                {
                    json.put(UserFormFactory.KEY_TYPE, authenticator.getAuthType());
                    if (callback.onSocialAuthDone(json))
                    {
                        logInAsyncWithJson(credentialsDTOFactory.create(json), callback);
                    }
                }
                catch (JSONException|ParseException ex)
                {
                    ex.printStackTrace();
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

    private static THCallback<UserProfileDTO> createCallbackForSignUpAsyncWithJson(final CredentialsDTO credentialsDTO, final LogInCallback callback)
    {
        return new THCallback<UserProfileDTO>()
        {
            @Override public void success(UserProfileDTO userProfileDTO, THResponse response)
            {
                saveCredentialsToUserDefaults(credentialsDTO);

                UserLoginDTO userLoginDTO = new UserLoginDTO();
                userLoginDTO.profileDTO = userProfileDTO;
                callback.done(userLoginDTO, null);
            }

            @Override public void failure(THException error)
            {
                callback.done(null, error);
                if (error.getCode() == ExceptionCode.DoNotRunBelow)
                {
                    Context context = Application.context();
                    if(context!=null){
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(AuthenticationActivity.INTENT_APPLICATION_VERSION_UPDATE));
                    }
                }
            }
        };
    }

    private static THCallback<UserLoginDTO> createCallbackForSignInAsyncWithJson(final CredentialsDTO credentialsDTO, final LogInCallback callback)
    {
        return new THCallback<UserLoginDTO>()
        {
            @Override public void success(UserLoginDTO userLoginDTO, THResponse response)
            {
                saveCredentialsToUserDefaults(credentialsDTO);
                callback.done(userLoginDTO, null);
            }

            @Override public void failure(THException error)
            {
                callback.done(null, error);
                if (error.getCode() == ExceptionCode.DoNotRunBelow)
                {
                    Context context = Application.context();
                    if(context!=null){
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(AuthenticationActivity.INTENT_APPLICATION_VERSION_UPDATE));
                    }
                }
            }
        };
    }

    public static void registerAuthenticationProvider(THAuthenticationProvider provider)
    {
        authenticationProviders.put(provider.getAuthType(), provider);
    }

    /**
     * @param credentialsDTO json data is from social media
     */
    public static void saveCredentialsToUserDefaults(CredentialsDTO credentialsDTO)
    {

        mainCredentialsPreference.setCredentials(credentialsDTO);
        typedCredentials.put(credentialsDTO.getAuthType(), credentialsDTO);
        credentialsSetPreference.replaceOrAddCredentials(credentialsDTO);
    }

    public static void clearCurrentUser()
    {
        typedCredentials.clear();
        dtoCacheUtil.get().clearUserRelatedCaches();
        currentUserId.delete();
        VisitedFriendListPrefs.clearVisitedIdList();

        CredentialsDTO currentCredentials = mainCredentialsPreference.getCredentials();
        if (currentCredentials != null)
        {
            THAuthenticationProvider currentProvider = authenticationProviders.get(currentCredentials.getAuthType());
            if (currentProvider != null)
            {
                currentProvider.deauthenticate();
            }
        }

        // clear all preferences
        mainCredentialsPreference.delete();
        credentialsSetPreference.delete();
        SharedPreferences.Editor prefEditor = sharedPreferences.get().edit();
        prefEditor.clear();
        prefEditor.commit();
    }

    public static void setAuthenticationMode(AuthenticationMode authenticationMode)
    {
        THUser.authenticationMode = authenticationMode;
    }

    public static THAuthenticationProvider getTHAuthenticationProvider()
    {
        return authenticator;
    }

    public static void removeCredential(String authenticationHeader)
    {
        if (typedCredentials == null)
        {
            return;
        }

        typedCredentials.remove(authenticationHeader);
        credentialsSetPreference.delete();
    }

    public static String getIMEI()
    {
        String imei = mDeviceIDStringPreference.get();
        if (imei.isEmpty())
        {
            TelephonyManager tm = (TelephonyManager)currentActivityHolder.get()
                    .getCurrentActivity().getSystemService(Context.TELEPHONY_SERVICE);
            String strIMEI = tm.getDeviceId();
            if (strIMEI.isEmpty() || strIMEI.contains("000000000000000"))
            {
                strIMEI = String.valueOf((int)Math.floor((Math.random() + 1) * GuideActivity.TIMES));
                strIMEI = strIMEI+String.valueOf((int)Math.floor((Math.random() + 1) * GuideActivity.TIMES2));
                mDeviceIDStringPreference.set(strIMEI);
            }
            else
            {
                mDeviceIDStringPreference.set(strIMEI);
            }
            return strIMEI;
        }
        return imei;
    }



}
