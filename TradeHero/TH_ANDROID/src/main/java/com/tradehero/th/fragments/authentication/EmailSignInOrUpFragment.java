package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.auth.EmailAuthenticationProvider;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.NetworkUtils;
import com.tradehero.th.widget.ValidationListener;
import com.tradehero.th.widget.ValidationMessage;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;

abstract public class EmailSignInOrUpFragment extends AuthenticationFragment implements View.OnClickListener, ValidationListener
{
    protected Button signButton;

    abstract public int getDefaultViewId ();
    abstract protected void initSetup(View view);
    abstract protected void forceValidateFields();

    abstract public boolean areFieldsValid();

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(getDefaultViewId(), container, false);

        initSetup(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override public void notifyValidation(ValidationMessage message)
    {
        if (message != null && !message.getStatus() && message.getMessage() != null)
        {
            THToast.show(message.getMessage());
        }
    }

    protected void handleSignInOrUpButtonClicked(View view)
    {
        DeviceUtil.dismissKeyboard(getActivity(), view);

        forceValidateFields();

        try
        {
            if (!NetworkUtils.isConnected(getActivity()))
            {
                THToast.show(R.string.network_error);
            }
            else if (!areFieldsValid ())
            {
                THToast.show(R.string.validation_please_correct);
            }
            else
            {
                register();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public JSONCredentials getUserFormJSON ()
    {
        return new JSONCredentials(getUserFormMap());
    }

    protected Map<String, Object> getUserFormMap ()
    {
        Map<String, Object> map = new HashMap<>();
        map.put(UserFormFactory.KEY_TYPE, EmailAuthenticationProvider.EMAIL_AUTH_TYPE);
        return map;
    }

    private void register() throws JSONException
    {
        // In fact we let the activity take care of the rest, as it listens for this button
        onClickListener.onClick(signButton);
    }
}
