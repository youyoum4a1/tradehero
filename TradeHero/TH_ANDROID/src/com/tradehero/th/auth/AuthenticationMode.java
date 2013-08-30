package com.tradehero.th.auth;

/** Created with IntelliJ IDEA. User: tho Date: 8/31/13 Time: 12:22 AM Copyright (c) TradeHero */
public enum AuthenticationMode
{
    SignUp("users"),
    SignIn("login"),
    Unknown("");

    private final String endPoint;

    AuthenticationMode(String endPoint)
    {
        this.endPoint = endPoint;
    }

    public String getEndPoint()
    {
        return endPoint;
    }
}
