package com.ayondo.academy.auth.tencent_qq;

import com.tencent.tauth.UiError;

class QQUiErrorException extends RuntimeException
{
    public final UiError error;

    public QQUiErrorException(UiError error)
    {
        super();
        this.error = error;
    }
}
