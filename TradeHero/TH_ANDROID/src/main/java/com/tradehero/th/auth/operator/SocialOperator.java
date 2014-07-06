package com.tradehero.th.auth.operator;

import android.app.ProgressDialog;
import org.jetbrains.annotations.NotNull;

public abstract class SocialOperator
{
    private ProgressDialog progressDialog;
    @NotNull private final String consumerKey;
    @NotNull private final String consumerSecret;
    private String authToken;
    private String authTokenSecret;

    public SocialOperator(@NotNull String consumerKey, @NotNull String consumerSecret)
    {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public void setProgressDialog(ProgressDialog progressDialog)
    {
        this.progressDialog = progressDialog;
    }

    protected void showProgress()
    {
        ProgressDialog progressDialogCopy = progressDialog;
        if (progressDialogCopy != null)
        {
            progressDialogCopy.show();
        }
    }

    protected void hideProgress()
    {
        ProgressDialog progressDialogCopy = progressDialog;
        if (progressDialogCopy != null)
        {
            progressDialogCopy.hide();
        }
    }

    @NotNull public String getConsumerKey()
    {
        return this.consumerKey;
    }

    @NotNull public String getConsumerSecret()
    {
        return this.consumerSecret;
    }

    public String getAuthToken()
    {
        return this.authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }

    public String getAuthTokenSecret()
    {
        return this.authTokenSecret;
    }

    public void setAuthTokenSecret(String authTokenSecret)
    {
        this.authTokenSecret = authTokenSecret;
    }

}
