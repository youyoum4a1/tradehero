package com.tradehero.th.auth;


public enum AuthenticationMode
{
    SignUp("users"),
    SignIn("login"),
    SignUpWithEmail("SignupByEmail"),
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
