package com.tradehero.th.api.translation.bing;

import com.tradehero.common.persistence.HasExpiration;
import com.tradehero.thm.R;
import com.tradehero.th.api.translation.TranslationToken;
import java.util.Calendar;
import java.util.Date;

public class BingTranslationToken extends TranslationToken
    implements HasExpiration
{
    public static final String TOKEN_TYPE = "MicrosoftTranslator";
    public static final String ACCESS_TOKEN_PREFIX = "Bearer %s";

    private Date expirationDate;
    public String tokenType;
    public String accessToken;
    private String expiresIn;
    public String scope;

    //<editor-fold desc="Constructors">
    public BingTranslationToken()
    {
        super();
        setExpirationDateSecondsInFuture(0);
    }

    public BingTranslationToken(
            String tokenType,
            String accessToken,
            String expiresIn,
            String scope)
    {
        super();
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        setExpiresIn(expiresIn);
        this.scope = scope;
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

    @Override public long getExpiresInSeconds()
    {
        return Math.max(
                0,
                Math.round((expirationDate.getTime() - Calendar.getInstance().getTime().getTime()) / 1000));
    }

    @Override public boolean isValid()
    {
        return getExpiresInSeconds() > 0;
    }

    @Override public int logoResId()
    {
        return R.drawable.logo_bing;
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
