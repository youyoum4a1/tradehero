package com.androidth.general.exception;

import android.support.annotation.StringRes;
import com.facebook.FacebookOperationCanceledException;
import com.androidth.general.common.utils.RetrofitHelper;
import com.androidth.general.R;
import com.androidth.general.api.ErrorMessageDTO;
import com.androidth.general.api.http.ResponseErrorCode;
import com.androidth.general.base.THApp;
import com.androidth.general.utils.Constants;
import java.util.List;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import timber.log.Timber;

public class THException extends Exception
{
    private ExceptionCode code;

    //<editor-fold desc="Constructors">
    public THException(Throwable cause)
    {
        initCause(cause);
    }

    public THException(String message)
    {
        initCause(new Exception(message));
    }
    //</editor-fold>

    @Override public Throwable initCause(Throwable throwable)
    {
        this.code = ExceptionCode.UnknownError;
        if (throwable instanceof RetrofitError)
        {
            RetrofitError error = (RetrofitError) throwable;
            if (error.isNetworkError())
            {
                this.code = ExceptionCode.NetworkError;
            }
            else if (error.getResponse() != null)
            {
                Response response = error.getResponse();
                if (response.getStatus() == 417)
                {
                    this.code = ExceptionCode.DoNotRunBelow;
                    List<Header> headers = response.getHeaders();
                    if (headers != null)
                    {
                        Header responseCodeHeader = new RetrofitHelper().findHeaderByName(headers, Constants.TH_ERROR_CODE);
                        if (responseCodeHeader != null)
                        {
                            ResponseErrorCode errorCode = ResponseErrorCode.getByCode(responseCodeHeader.getValue());
                            if (errorCode != null)
                            {
                                switch (errorCode)
                                {
                                    case OutDatedVersion:
                                        this.code = ExceptionCode.DoNotRunBelow;
                                        break;

                                    case ExpiredSocialToken:
                                        this.code = ExceptionCode.RenewSocialToken;
                                        break;
                                }
                            }
                        }
                    }
                }
                else if (response.getStatus() != 200) // Bad Request
                {
                    ErrorMessageDTO dto = null;
                    // surprisingly, server does return garbage sometime
                    try
                    {
                        dto = (ErrorMessageDTO) error.getBodyAs(ErrorMessageDTO.class);
                    }
                    catch (Exception ex)
                    {
                        Timber.e(ex, ex.getMessage());
                    }
                    this.code = ExceptionCode.UnknownError;
                    String errorMessage = dto != null
                            ? THApp.context().getString(R.string.server_response) + dto.message
                            : THApp.context().getString(R.string.error_unknown);
                    return super.initCause(new Exception(errorMessage));
                }
            }
        }
        else if (throwable instanceof FacebookOperationCanceledException)
        {
            this.code = ExceptionCode.UserCanceled;
        }
        return super.initCause(throwable);
    }

    @Override public String getMessage()
    {
        if (code != ExceptionCode.UnknownError)
        {
            return code.getErrorMessage();
        }
        else if (code.isCanContinue())
        {
            return getCause().getMessage();
        }
        else
        {
            return getCause().getMessage();
        }
    }

    public ExceptionCode getCode()
    {
        return code;
    }

    public enum ExceptionCode
    {
        UserCanceled(true, R.string.error_canceled),
        UnknownError(R.string.error_unknown),
        NetworkError(R.string.error_network_connection),
        DoNotRunBelow(R.string.please_update),
        RenewSocialToken(R.string.please_update_token_title),
        ;

        private final boolean canContinue;
        @StringRes private final int errorMessage;

        private ExceptionCode(@StringRes int errorMessageResourceId)
        {
            this(false, errorMessageResourceId);
        }

        private ExceptionCode(boolean canContinue, @StringRes int errorMessageResourceId)
        {
            this.canContinue = canContinue;
            this.errorMessage = errorMessageResourceId;
        }

        public String getErrorMessage()
        {
            return THApp.context().getString(errorMessage);
        }

        public boolean isCanContinue()
        {
            return canContinue;
        }

        public THException toException()
        {
            THException exception = new THException(getErrorMessage());
            exception.code = this;
            return exception;
        }
    }
}