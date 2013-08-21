package com.tradehero.th.auth.linkedin;

/** Created with IntelliJ IDEA. User: tho Date: 8/21/13 Time: 12:48 PM Copyright (c) TradeHero */
public class LinkedIn
{
    private String consumerSecret;
    private String consumerKey;

    public LinkedIn(String consumerKey, String consumerSecret)
    {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public void setConsumerKey(String consumerKey)
    {
    }

    public void setConsumerSecret(String consumerSecret)
    {
        this.consumerSecret = consumerSecret;
    }
    public String getAuthType()
{
    return "linkedin";
}
}
