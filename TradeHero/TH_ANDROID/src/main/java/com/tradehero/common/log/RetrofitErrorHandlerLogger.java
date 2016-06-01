package com.tradehero.common.log;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import com.tradehero.common.utils.RetrofitHelper;
import com.tradehero.common.utils.THToast;
import com.ayondo.academy.R;
import com.ayondo.academy.activities.ActivityUtil;
import com.ayondo.academy.api.http.ResponseErrorCode;
import com.ayondo.academy.misc.exception.THException;
import com.ayondo.academy.utils.Constants;
import dagger.Lazy;
import java.net.SocketTimeoutException;
import java.util.List;
import javax.inject.Inject;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import timber.log.Timber;

public class RetrofitErrorHandlerLogger implements ErrorHandler
{
    @NonNull final Lazy<RetrofitHelper> retrofitHelper;
    @NonNull final LocalBroadcastManager localBroadcastManager;

    //<editor-fold desc="Constructors">
    @Inject public RetrofitErrorHandlerLogger(
            @NonNull Context context,
            @NonNull Lazy<RetrofitHelper> retrofitHelper)
    {
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        this.retrofitHelper = retrofitHelper;
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
                            localBroadcastManager.sendBroadcast(ActivityUtil.getIntentUpgrade());
                            return;

                        case ExpiredSocialToken:
                            THToast.show(R.string.please_update_token_description);
                            localBroadcastManager.sendBroadcast(ActivityUtil.getIntentSocialToken());
                            return;
                    }
                }
            }
        }
        Timber.e(new Exception(), "Response 417 not handled, headers: %s, responseCodeHeader: %s, codeHeaderValue: %s", headers, responseCodeHeader, codeHeaderValue);
        THToast.show(R.string.error_unknown);
    }
}
