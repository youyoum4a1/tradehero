package com.tradehero.th.auth;

import android.content.Context;
import com.tradehero.th.auth.linkedin.LinkedIn;
import java.lang.ref.WeakReference;
import org.json.JSONObject;

/** Created with IntelliJ IDEA. User: tho Date: 8/21/13 Time: 12:49 PM Copyright (c) TradeHero */
public class LinkedInAuthenticationProvider
        implements THAuthenticationProvider
{
    private final LinkedIn linkedIn;
    private String authType;
    private Context context;
    private WeakReference<Context> baseContext;

    public LinkedInAuthenticationProvider(LinkedIn linkedIn)
    {
        this.linkedIn = linkedIn;
    }

    public void setContext(Context context)
    {
        this.baseContext = new WeakReference(context);
    }

    @Override public void authenticate(THAuthenticationCallback callback)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void deauthenticate()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public boolean restoreAuthentication(JSONObject paramJSONObject)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void cancel()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getAuthType()
    {
        return authType;
    }
}
