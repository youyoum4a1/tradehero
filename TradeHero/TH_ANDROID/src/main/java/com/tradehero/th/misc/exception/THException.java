package com.tradehero.th.misc.exception;

import com.facebook.FacebookOperationCanceledException;
import com.tradehero.th.R;
import com.tradehero.th.api.ErrorMessageDTO;
import com.tradehero.th.base.Application;
import retrofit.RetrofitError;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 5:20 PM Copyright (c) TradeHero */
public class THException extends Exception
{
    private ExceptionCode code;

    public THException(Throwable cause)
    {
        initCause(cause);
    }

    public THException(String message)
    {
        initCause(new Exception(message));
    }

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
            else if (error.getResponse().getStatus() == 417)
            {
                this.code = ExceptionCode.DoNotRunBellow;
            }
            else if (error.getResponse() != null && error.getResponse().getStatus() != 200) // Bad Request
            {
                ErrorMessageDTO dto = null;
                // surprisingly, server does return garbage sometime
                try
                {
                    dto = (ErrorMessageDTO) error.getBodyAs(ErrorMessageDTO.class);
                }
                catch (Exception ex)
                {
                    Timber.d(ex.getMessage());
                }
                this.code = ExceptionCode.UnknownError;
                String errorMessage = dto != null ? Application.getResourceString(R.string.server_response) + dto.Message : Application.getResourceString(R.string.error_unknown);
                return super.initCause(new Exception(errorMessage));
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
        DoNotRunBellow(R.string.please_update);

        private boolean canContinue;
        private String errorMessage;

        private ExceptionCode(boolean canContinue, String errorMessage)
        {
            init(canContinue, errorMessage);
        }

        private ExceptionCode(boolean canContinue, int errorMessageResourceId)
        {
            init(canContinue, Application.context().getString(errorMessageResourceId));
        }

        private ExceptionCode(String errorMessage)
        {
            init(false, errorMessage);
        }

        private ExceptionCode(int errorMessageResourceId)
        {
            init(false, Application.context().getString(errorMessageResourceId));
        }

        private void init(boolean canContinue, String errorMessage)
        {
            this.canContinue = canContinue;
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage()
        {
            return errorMessage;
        }

        public boolean isCanContinue()
        {
            return canContinue;
        }

        public THException toException()
        {
            THException exception = new THException(errorMessage);
            exception.code = this;
            return exception;
        }
    }
}
