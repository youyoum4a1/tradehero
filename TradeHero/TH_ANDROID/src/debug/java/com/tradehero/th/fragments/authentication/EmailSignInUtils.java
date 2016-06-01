package com.ayondo.academy.fragments.authentication;

import android.support.annotation.NonNull;
import android.widget.TextView;
import com.ayondo.academy.R;

public class EmailSignInUtils
{
    public static void populateDefaults(@NonNull TextView email, @NonNull TextView password)
    {
        email.setText(R.string.test_email);
        password.setText(R.string.test_password);
    }
}
