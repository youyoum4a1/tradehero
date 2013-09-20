package com.tradehero.th.auth.operator;

import android.app.ProgressDialog;

/** Created with IntelliJ IDEA. User: tho Date: 8/22/13 Time: 6:26 PM Copyright (c) TradeHero */
public abstract class SocialOperator
{
    private ProgressDialog progressDialog;
    private String consumerKey;
    private String consumerSecret;
    private String authToken;
    private String authTokenSecret;

    public void setProgressDialog(ProgressDialog progressDialog)
    {
        this.progressDialog = progressDialog;
    }

    protected void showProgress()
    {
        if (progressDialog != null)
        {
            progressDialog.show();
        }
    }

    protected void hideProgress()
    {
        if (progressDialog != null)
        {
            progressDialog.hide();
        }
    }

    public String getConsumerKey()
    {
        return this.consumerKey;
    }

    public void setConsumerKey(String consumerKey)
    {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret()
    {
        return this.consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret)
    {
        this.consumerSecret = consumerSecret;
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
