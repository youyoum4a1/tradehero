package com.tradehero.th.loaders;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/24/14 Time: 11:18 AM Copyright (c) TradeHero
 */
public class ContactEntry
{
    private String email;
    private String name;
    private Uri photoUri;

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Uri getPhotoUri()
    {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri)
    {
        this.photoUri = photoUri;
    }
}
