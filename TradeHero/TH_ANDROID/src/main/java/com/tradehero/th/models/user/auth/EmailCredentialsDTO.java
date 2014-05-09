package com.tradehero.th.models.user.auth;

public class EmailCredentialsDTO implements CredentialsDTO
{
    public static final String EMAIL_AUTH_TYPE = "Basic";

    public final String email;
    public final String password;

    public EmailCredentialsDTO(String email, String password)
    {
        super();
        this.email = email;
        this.password = password;
    }

    @Override public String getAuthType()
    {
        return EMAIL_AUTH_TYPE;
    }
}
