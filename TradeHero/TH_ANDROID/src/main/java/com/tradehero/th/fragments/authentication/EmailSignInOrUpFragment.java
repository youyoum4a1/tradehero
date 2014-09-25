package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.InjectView;
import com.tradehero.common.utils.OnlineStateReceiver;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.models.user.auth.EmailCredentialsDTO;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.widget.ValidationListener;
import com.tradehero.th.widget.ValidationMessage;
import java.util.HashMap;
import java.util.Map;

abstract public class EmailSignInOrUpFragment extends Fragment
        implements ValidationListener
{
    abstract public int getDefaultViewId ();
    abstract protected void initSetup(View view);
    abstract protected void forceValidateFields();

    abstract public boolean areFieldsValid();

    @InjectView(R.id.authentication_back_button) ImageView backButton;

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
        DeviceUtil.dismissKeyboard(view);

        forceValidateFields();

        if (!OnlineStateReceiver.isOnline(getActivity()))
        {
            THToast.show(R.string.network_error);
        }
        else if (!areFieldsValid())
        {
            THToast.show(R.string.validation_please_correct);
        }
        else
        {
            //register();
        }
    }

    public JSONCredentials getUserFormJSON ()
    {
        return new JSONCredentials(getUserFormMap());
    }

    protected Map<String, Object> getUserFormMap ()
    {
        Map<String, Object> map = new HashMap<>();
        map.put(UserFormFactory.KEY_TYPE, EmailCredentialsDTO.EMAIL_AUTH_TYPE);
        return map;
    }
}
