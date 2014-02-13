package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.widget.MatchingPasswordText;
import com.tradehero.th.widget.ServerValidatedEmailText;
import com.tradehero.th.widget.ServerValidatedUsernameText;
import com.tradehero.th.widget.ValidatedPasswordText;
import com.tradehero.th.widget.ValidationListener;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xavier on 1/7/14.
 */
public class ProfileInfoView extends LinearLayout
{
    public static final String TAG = ProfileInfoView.class.getSimpleName();

    @InjectView(R.id.authentication_sign_up_email) ServerValidatedEmailText email;
    @InjectView(R.id.authentication_sign_up_password) ValidatedPasswordText password;
    @InjectView(R.id.authentication_sign_up_confirm_password) MatchingPasswordText confirmPassword;
    @InjectView(R.id.authentication_sign_up_username) ServerValidatedUsernameText displayName;
    @InjectView(R.id.et_firstname) EditText firstName;
    @InjectView(R.id.et_lastname) EditText lastName;
    ProgressDialog progressDialog;

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

        ButterKnife.inject(this);
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

    public void setNullOnFields()
    {
        email = null;
        password = null;
        confirmPassword = null;
        displayName = null;
        firstName = null;
        lastName = null;
        progressDialog = null;
    }

    public void populateUserFormMap(Map<String, Object> map)
    {
        map.put(UserFormFactory.KEY_EMAIL, email.getText());
        map.put(UserFormFactory.KEY_PASSWORD, password.getText());
        map.put(UserFormFactory.KEY_PASSWORD_CONFIRM, confirmPassword.getText());
        map.put(UserFormFactory.KEY_DISPLAY_NAME, displayName.getText());
        map.put(UserFormFactory.KEY_FIRST_NAME, firstName.getText());
        map.put(UserFormFactory.KEY_LAST_NAME, lastName.getText());
        // TODO add profile picture
    }

    public void populate(UserBaseDTO userBaseDTO)
    {
        this.firstName.setText(userBaseDTO.firstName);
        this.lastName.setText(userBaseDTO.lastName);
        this.displayName.setText(userBaseDTO.displayName);
        this.displayName.setOriginalUsernameValue(userBaseDTO.displayName);
    }

    public void populateCredentials(JSONObject credentials)
    {
        String emailValue = null, passwordValue = null;
        try
        {
            emailValue = credentials.getString("email");
            passwordValue = credentials.getString("password");
        }
        catch (JSONException e)
        {
            THLog.e(TAG, "populateCurrentUser", e);
        }
        this.email.setText(emailValue);
        this.password.setText(passwordValue);
        this.confirmPassword.setText(passwordValue);
    }
}
