package com.tradehero.th.api.translation.bing;

import com.tradehero.th.api.translation.TranslationToken;
import java.util.Calendar;
import java.util.Date;

public class BingTranslationToken extends TranslationToken
{
    public static final String TOKEN_TYPE = "MicrosoftTranslator";
    public static final String ACCESS_TOKEN_PREFIX = "Bearer %s";

    private Date expirationDate;
    public String tokenType;
    public String accessToken;
    public String expiresIn;
    public String scope;

    //<editor-fold desc="Constructors">
    public BingTranslationToken()
    {
        super();
        type = TOKEN_TYPE;
        setExpirationDateSecondsInFuture(0);
    }

    public BingTranslationToken(String tokenType, String accessToken, String expiresIn,
            String scope)
    {
        super();
        type = TOKEN_TYPE;
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        setExpiresIn(expiresIn);
        this.scope = scope;
    }

    public BingTranslationToken(TranslationToken other, Class<? extends BingTranslationToken> myClass)
    {
        super(other, myClass);
        type = TOKEN_TYPE;
    }
    //</editor-fold>

    public String getTokenType()
    {
        return tokenType;
    }

    public void setTokenType(String tokenType)
    {
        this.tokenType = tokenType;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    public String getPrefixedAccessToken()
    {
        return String.format(ACCESS_TOKEN_PREFIX, accessToken);
    }

    public String getExpiresIn()
    {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn)
    {
        this.expiresIn = expiresIn;
        setExpirationDateSecondsInFuture(Integer.parseInt(expiresIn));
    }

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }

    protected void setExpirationDateSecondsInFuture(int seconds)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, seconds);
        this.expirationDate = calendar.getTime();
    }

    @Override public boolean isValid()
    {
        return Calendar.getInstance().getTime().getTime() < expirationDate.getTime();
    }

    @Override
    public String toString()
    {
        return "BingTranslationToken{" +
                "tokenType='" + tokenType + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", expiresIn='" + expiresIn + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}
