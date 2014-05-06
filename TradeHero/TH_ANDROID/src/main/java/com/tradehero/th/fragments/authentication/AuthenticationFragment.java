package com.tradehero.th.fragments.authentication;

import android.app.Activity;
import android.view.View;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.th.auth.AuthenticationMode;


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

    @Override public void onDetach()
    {
       onClickListener = null;
        super.onDetach();
    }

    public abstract AuthenticationMode getAuthenticationMode();
}
