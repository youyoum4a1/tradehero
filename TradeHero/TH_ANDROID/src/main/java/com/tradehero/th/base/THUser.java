package com.tradehero.th.base;

import android.app.Activity;
import android.content.DialogInterface;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.R;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.auth.THAuthenticationProvider;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.misc.exception.THException.ExceptionCode;
import com.tradehero.th.models.user.auth.CredentialsDTO;
import com.tradehero.th.models.user.auth.CredentialsDTOFactory;
import com.tradehero.th.models.user.auth.CredentialsSetPreference;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.prefs.SavedPushDeviceIdentifier;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

public class THUser
{
    private static AuthenticationMode authenticationMode;
    private static THAuthenticationProvider authenticator;
    private static final Map<String, THAuthenticationProvider> authenticationProviders = new HashMap<>();

    private static HashMap<String, CredentialsDTO> typedCredentials;

    @Inject static MainCredentialsPreference mainCredentialsPreference;
    @Inject static CredentialsSetPreference credentialsSetPreference;

    @Inject @SavedPushDeviceIdentifier static Lazy<StringPreference> savedPushIdentifier;
    @Inject static Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject static Lazy<SessionServiceWrapper> sessionServiceWrapper;
    @Inject static Lazy<AlertDialogUtil> alertDialogUtil;
    @Inject static CredentialsDTOFactory credentialsDTOFactory;

    public static void logInWithAsync(String authType, LogInCallback callback)
    {
        //FIXME/refactor to be replaced
    }

    public static void logInAsyncWithJson(final CredentialsDTO credentialsDTO, final LogInCallback callback)
    {
        //FIXME/refactor to be replaced with the new one
    }

    public static void registerAuthenticationProvider(THAuthenticationProvider provider)
    {
        // FIXME/refactor to be removed
        //authenticationProviders.put(provider.getAuthType(), provider);
    }

    private static void checkNeedForUpgrade(THException error)
    {
        // FIXME CurrentActivityHolder has been removed, refactor THUser!!!
        //if (error.getCode() == ExceptionCode.DoNotRunBelow)
        //{
        //    final Activity currentActivity = activityProvider.get();
        //    alertDialogUtil.get().popWithOkCancelButton(
        //            currentActivity,
        //            R.string.upgrade_needed,
        //            R.string.please_update,
        //            R.string.update_now,
        //            R.string.later,
        //            new DialogInterface.OnClickListener()
        //            {
        //                @Override public void onClick(DialogInterface dialog, int which)
        //                {
        //                    THToast.show(R.string.update_guide);
        //                    if (currentActivity != null)
        //                    {
        //                        marketUtilLazy.get().showAppOnMarket(currentActivity);
        //                        currentActivity.finish();
        //                    }
        //                }
        //            });
        //}
    }

    private static void checkNeedToRenewSocialToken(THException error, CredentialsDTO credentialsDTO)
    {
        if (error.getCode() == ExceptionCode.RenewSocialToken)
        {
            mainCredentialsPreference.delete();

            // FIXME since currentActivityHolder has been removed, refactor THUser
            final Activity currentActivity = null; // activityProvider.get();

            alertDialogUtil.get().popWithOkCancelButton(currentActivity,
                    R.string.please_update_token_title,
                    R.string.please_update_token_description,
                    R.string.ok,
                    R.string.later,
                    new DialogInterface.OnClickListener()
                    {
                        @Override public void onClick(DialogInterface dialog, int which)
                        {
                        }
                    });
        }
    }

    /**
     * @param credentialsDTO json data is from social media
     */
    private static void saveCredentialsToUserDefaults(CredentialsDTO credentialsDTO)
    {
        Timber.d("%d authentication tokens loaded", typedCredentials.size());

        mainCredentialsPreference.setCredentials(credentialsDTO);
        mainCredentialsPreference.setCredentials(credentialsDTO);
        typedCredentials.put(credentialsDTO.getAuthType(), credentialsDTO);
        credentialsSetPreference.replaceOrAddCredentials(credentialsDTO);
    }
}
