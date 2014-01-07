package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.tradehero.th.R;
import com.tradehero.th.widget.MatchingPasswordText;
import com.tradehero.th.widget.ServerValidatedEmailText;
import com.tradehero.th.widget.ServerValidatedUsernameText;
import com.tradehero.th.widget.ValidatedPasswordText;
import com.tradehero.th.widget.ValidationListener;

/**
 * Created by xavier on 1/7/14.
 */
public class ProfileInfoView extends LinearLayout
{
    public static final String TAG = ProfileInfoView.class.getSimpleName();

    public ServerValidatedEmailText email;
    public ValidatedPasswordText password;
    public MatchingPasswordText confirmPassword;
    public ServerValidatedUsernameText displayName;
    public EditText firstName, lastName;
    public ProgressDialog progressDialog;

    //<editor-fold desc="Constructors">
    public ProfileInfoView(Context context)
    {
        super(context);
    }

    public ProfileInfoView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ProfileInfoView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        email = (ServerValidatedEmailText) findViewById(R.id.authentication_sign_up_email);
        password = (ValidatedPasswordText) findViewById(R.id.authentication_sign_up_password);
        confirmPassword = (MatchingPasswordText) findViewById(R.id.authentication_sign_up_confirm_password);
        displayName = (ServerValidatedUsernameText) findViewById(R.id.authentication_sign_up_username);
        firstName = (EditText) findViewById(R.id.et_firstname);
        lastName = (EditText) findViewById(R.id.et_lastname);

    }

    public void forceValidateFields()
    {
        if (email != null)
        {
            email.forceValidate();
        }
        if (password != null)
        {
            password.forceValidate();
        }
        if (confirmPassword != null)
        {
            confirmPassword.forceValidate();
        }
        if (displayName != null)
        {
            displayName.forceValidate();
        }
    }

    public boolean areFieldsValid()
    {
        return (email == null || email.isValid()) &&
                (password == null || password.isValid()) &&
                (confirmPassword == null || confirmPassword.isValid()) &&
                (displayName == null || displayName.isValid());
    }

    public void setOnTouchListenerOnFields(View.OnTouchListener touchListener)
    {
        if (email != null)
        {
            email.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..
        }
        if (password != null)
        {
            password.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..
        }
        if (confirmPassword != null)
        {
            confirmPassword.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..
        }
        if (displayName != null)
        {
            displayName.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..
        }
        if (firstName != null)
        {
            firstName.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..
        }
        if (lastName != null)
        {
            lastName.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..
        }
    }

    public void addValidationListenerOnFields(ValidationListener listener)
    {
        if (email != null)
        {
            email.addListener(listener);
        }
        if (password != null)
        {
            password.addListener(listener);
        }
        if (confirmPassword != null)
        {
            confirmPassword.addListener(listener);
        }
        if (displayName != null)
        {
            displayName.addListener(listener);
        }
    }

    public void removeAllListenersOnFields()
    {
        if (email != null)
        {
            email.removeAllListeners();
        }
        if (password != null)
        {
            password.removeAllListeners();
        }
        if (confirmPassword != null)
        {
            confirmPassword.removeAllListeners();
        }
        if (displayName != null)
        {
            displayName.removeAllListeners();
        }
    }
}
