package com.androidth.general.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class THAccountAuthenticatorService extends Service
{
    private THAccountAuthenticator mAuthenticator;

    public THAccountAuthenticatorService()
    {
        mAuthenticator = new THAccountAuthenticator(this);
    }

    @Override public IBinder onBind(Intent intent)
    {
        return mAuthenticator.getIBinder();
    }
}
