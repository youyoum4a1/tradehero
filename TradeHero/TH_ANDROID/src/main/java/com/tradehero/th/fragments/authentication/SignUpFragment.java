package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.R;
import com.tradehero.th.auth.AuthenticationMode;
import javax.inject.Inject;

/**
 * Fragment for signing up
 */
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

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(getViewId(), container, false);
        setOnClickListener(view);
        view.findViewById(R.id.authentication_by_sign_in_button).setOnClickListener(onClickListener);
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }
}
