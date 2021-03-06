package com.tradehero.common.log;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.tradehero.common.utils.RetrofitHelper;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.ForSocialToken;
import com.tradehero.th.activities.ForUpgrade;
import com.tradehero.th.api.http.ResponseErrorCode;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.utils.Constants;
import dagger.Lazy;
import java.net.SocketTimeoutException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import android.support.annotation.NonNull;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import timber.log.Timber;

public class RetrofitErrorHandlerLogger implements ErrorHandler
{
    @NonNull final Lazy<RetrofitHelper> retrofitHelper;
    @NonNull final Lazy<LocalBroadcastManager> localBroadcastManager;
    @NonNull final Provider<Intent> upgradeIntentProvider;
    @NonNull final Provider<Intent> socialTokenIntentProvider;

    //<editor-fold desc="Constructors">
    @Inject public RetrofitErrorHandlerLogger(
            @NonNull Lazy<RetrofitHelper> retrofitHelper,
            @NonNull Lazy<LocalBroadcastManager> localBroadcastManager,
            @NonNull @ForUpgrade Provider<Intent> upgradeIntentProvider,
            @NonNull @ForSocialToken Provider<Intent> socialTokenIntentProvider)
    {
        this.retrofitHelper = retrofitHelper;
        this.localBroadcastManager = localBroadcastManager;
        this.upgradeIntentProvider = upgradeIntentProvider;
        this.socialTokenIntentProvider = socialTokenIntentProvider;
    }
    //</editor-fold>

    @Override public Throwable handleError(RetrofitError retrofitError)
    {
        if (retrofitError != null)
        {
            Throwable cause = retrofitError.getCause();
            Response response = retrofitError.getResponse();
            if (cause instanceof SocketTimeoutException)
            {
                // Attempt at reporting server sluggishness
                Timber.e(cause, "Retrofit timeout on %s", retrofitError.getUrl());
            }
            else if (retrofitError.isNetworkError())
            {
                Integer responseCode = null;
                if (response != null)
                {
                    responseCode = response.getStatus();
                }
                if (responseCode != null)
                {
                    Timber.e(retrofitError, "Response Code %d on %s", responseCode, retrofitError.getUrl());
                }
            }
            else if (response != null && response.getStatus() == 417)
            {
                THToast.show(new THException(retrofitError));
                handleRenewError(response);
            }
        }
        return retrofitError;
    }

    private void handleRenewError(@NonNull Response response)
    {
        List<Header> headers = response.getHeaders();
        Header responseCodeHeader = null;
        String codeHeaderValue = null;
        if (headers != null)
        {
            responseCodeHeader = retrofitHelper.get().findHeaderByName(headers, Constants.TH_ERROR_CODE);
            if (responseCodeHeader != null)
            {
                codeHeaderValue = responseCodeHeader.getValue();
                ResponseErrorCode errorCode = ResponseErrorCode.getByCode(codeHeaderValue);
                if (errorCode != null)
                {
                    switch (errorCode)
                    {
                        case OutDatedVersion:
                            THToast.show(R.string.upgrade_needed);
                            localBroadcastManager.get().sendBroadcast(upgradeIntentProvider.get());
                            return;

                        case ExpiredSocialToken:
                            THToast.show(R.string.please_update_token_description);
                            localBroadcastManager.get().sendBroadcast(socialTokenIntentProvider.get());
                            return;
                    }
                }
            }
        }
        Timber.e(new Exception(), "Response 417 not handled, headers: %s, responseCodeHeader: %s, codeHeaderValue: %s", headers, responseCodeHeader, codeHeaderValue);
        THToast.show(R.string.error_unknown);
    }
}
