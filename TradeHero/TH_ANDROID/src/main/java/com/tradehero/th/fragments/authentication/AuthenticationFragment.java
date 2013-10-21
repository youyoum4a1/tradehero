package com.tradehero.th.fragments.authentication;

import android.app.Activity;
import android.view.View;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.th.auth.AuthenticationMode;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 12:16 PM Copyright (c) TradeHero */
public abstract class AuthenticationFragment extends SherlockFragment
{
    protected View.OnClickListener onClickListener;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        if (activity instanceof View.OnClickListener)
        {
            onClickListener = (View.OnClickListener) activity;
        }
    }

    public abstract AuthenticationMode getAuthenticationMode();
}
