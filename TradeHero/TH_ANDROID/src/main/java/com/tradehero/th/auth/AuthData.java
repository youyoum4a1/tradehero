package com.tradehero.th.auth;

import java.util.Date;

public class AuthData
{
    public final String accessToken;
    public final Date expirationDate;

    public AuthData(String accessToken, Date expirationDate)
    {
        this.accessToken = accessToken;
        this.expirationDate = expirationDate;
    }
}
