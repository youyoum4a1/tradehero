package com.androidth.general.common.log;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import com.androidth.general.common.utils.RetrofitHelper;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.activities.ActivityUtil;
import com.androidth.general.api.http.ResponseErrorCode;
import com.androidth.general.network.NetworkConstants;
import com.androidth.general.utils.Constants;
import dagger.Lazy;

import java.io.IOException;
import javax.inject.Inject;

import okhttp3.Interceptor;
import retrofit.client.Header;
import timber.log.Timber;

public class RetrofitErrorHandlerLogger implements Interceptor
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


    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {

        okhttp3.Response response = chain.proceed(chain.request());

        if (response != null && response.code() == 417)
        {
//            THToast.show(new THException(retrofitError));
            handleRenewError(response);
        }
        return response;
    }

    private void handleRenewError(@NonNull okhttp3.Response response)
    {
        okhttp3.Headers headers = response.headers();
        Header responseCodeHeader = null;
        String codeHeaderValue = null;
        if (headers != null)
        {
            codeHeaderValue = retrofitHelper.get().findHeaderCodeByName(headers, NetworkConstants.TH_ERROR_CODE);
            if (codeHeaderValue != null)
            {
                ResponseErrorCode errorCode = ResponseErrorCode.getByCode(codeHeaderValue);
                if (errorCode != null)
                {
                    switch (errorCode)
                    {
                        case OutDatedVersion:
//                            THToast.show(R.string.upgrade_needed);
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





//    public Throwable handleError(RetrofitError retrofitError)
//    {
//        if (retrofitError != null)
//        {
//            Throwable cause = retrofitError.getCause();
//            Response response = retrofitError.getResponse();
//            if (cause instanceof SocketTimeoutException)
//            {
//                // Attempt at reporting server sluggishness
//                Timber.e(cause, "Retrofit timeout on %s", retrofitError.getUrl());
//            }
//            else if (retrofitError.isNetworkError())
//            {
//                Integer responseCode = null;
//                if (response != null)
//                {
//                    responseCode = response.getStatus();
//                }
//                if (responseCode != null)
//                {
//                    Timber.e(retrofitError, "Response Code %d on %s", responseCode, retrofitError.getUrl());
//                }
//            }
//            else if (response != null && response.getStatus() == 417)
//            {
//                THToast.show(new THException(retrofitError));
//                handleRenewError(response);
//            }
//        }
//        return retrofitError;
//    }

//    private void handleRenewError(@NonNull Response response)
//    {
//        List<Header> headers = response.getHeaders();
//        Header responseCodeHeader = null;
//        String codeHeaderValue = null;
//        if (headers != null)
//        {
//            responseCodeHeader = retrofitHelper.get().findHeaderByName(headers, Constants.TH_ERROR_CODE);
//            if (responseCodeHeader != null)
//            {
//                codeHeaderValue = responseCodeHeader.getValue();
//                ResponseErrorCode errorCode = ResponseErrorCode.getByCode(codeHeaderValue);
//                if (errorCode != null)
//                {
//                    switch (errorCode)
//                    {
//                        case OutDatedVersion:
//                            THToast.show(R.string.upgrade_needed);
//                            localBroadcastManager.sendBroadcast(ActivityUtil.getIntentUpgrade());
//                            return;
//
//                        case ExpiredSocialToken:
//                            THToast.show(R.string.please_update_token_description);
//                            localBroadcastManager.sendBroadcast(ActivityUtil.getIntentSocialToken());
//                            return;
//                    }
//                }
//            }
//        }
//        Timber.e(new Exception(), "Response 417 not handled, headers: %s, responseCodeHeader: %s, codeHeaderValue: %s", headers, responseCodeHeader, codeHeaderValue);
//        THToast.show(R.string.error_unknown);
//    }
}
