package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.auth.AuthenticationMode;

public class SignUpFragment extends SignInOrUpFragment
{
    @Override protected int getViewId()
    {
        return R.layout.authentication_sign_up;
    }

    @Override protected int getEmailSignUpViewId()
    {
        return R.id.authentication_email_sign_up_link;
    }

    @Override public AuthenticationMode getAuthenticationMode()
    {
        return AuthenticationMode.SignUp;
    }
}
