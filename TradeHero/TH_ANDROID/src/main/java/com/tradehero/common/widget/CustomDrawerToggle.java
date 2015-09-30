package com.tradehero.common.widget;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;

public class CustomDrawerToggle extends ActionBarDrawerToggle
{
    float mPos = 0;

    public CustomDrawerToggle(Activity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes)
    {
        super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
    }

    @Override public void onDrawerSlide(View drawerView, float slideOffset)
    {
        super.onDrawerSlide(drawerView, slideOffset);
        mPos = slideOffset;
    }

    public float getPosition()
    {
        return mPos;
    }
}
