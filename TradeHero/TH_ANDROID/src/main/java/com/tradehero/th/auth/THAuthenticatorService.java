package com.tradehero.th.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class THAuthenticatorService extends Service
{
    private THAuthenticator mAuthenticator;

    public THAuthenticatorService()
    {
        mAuthenticator = new THAuthenticator(this);
    }

    @Override public IBinder onBind(Intent intent)
    {
        return mAuthenticator.getIBinder();
    }
}
