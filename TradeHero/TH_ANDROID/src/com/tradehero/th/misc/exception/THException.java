package com.tradehero.th.misc.exception;

import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 5:20 PM Copyright (c) TradeHero */
public class THException extends Exception
{

    private ExceptionCode code;

    public THException(Throwable cause)
    {
        super(cause);
        init();
    }

    public THException(String message)
    {
        super(new Exception(message));
        init();
    }

    private void init()
    {
        this.code = ExceptionCode.UnknownError;
        if (getCause() instanceof RetrofitError)
        {
            RetrofitError error = (RetrofitError) getCause();
            if (error.isNetworkError())
            {
                this.code = ExceptionCode.NetworkError;
            }
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
        NetworkError(R.string.error_network_connection);

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
    }
}
