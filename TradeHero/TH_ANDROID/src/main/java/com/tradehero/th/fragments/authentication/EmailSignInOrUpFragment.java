package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import butterknife.ButterKnife;
import com.tradehero.common.utils.OnlineStateReceiver;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.models.user.auth.EmailCredentialsDTO;
import com.tradehero.th.utils.DeviceUtil;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;

abstract public class EmailSignInOrUpFragment extends Fragment
{
    abstract protected void initSetup(View view);

    abstract public boolean areFieldsValid();

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initSetup(view);
    }

    protected void handleSignInOrUpButtonClicked(View view)
    {
        DeviceUtil.dismissKeyboard(view);

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

    public JSONCredentials getUserFormJSON()
    {
        return new JSONCredentials(getUserFormMap());
    }

    protected Map<String, Object> getUserFormMap()
    {
        Map<String, Object> map = new HashMap<>();
        map.put(UserFormDTO.KEY_TYPE, EmailCredentialsDTO.EMAIL_AUTH_TYPE);
        return map;
    }

    public abstract Observable<AuthData> obtainAuthData();
}