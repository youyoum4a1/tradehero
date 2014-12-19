package com.tradehero.chinabuild.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.user.auth.CredentialsDTO;
import com.tradehero.th.models.user.auth.EmailCredentialsDTO;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class MyEditAccountFragment extends DashboardFragment implements View.OnClickListener
{
    @InjectView(R.id.authentication_sign_in_email) EditText mEmail;
    @InjectView(R.id.et_pwd_login) EditText mPassWord;
    @InjectView(R.id.btn_login) Button mUpdateButton;
    @Inject MainCredentialsPreference mainCredentialsPreference;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    private MiddleCallback<UserProfileDTO> middleCallbackUpdateUserProfile;
    private String mSavedPassword;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.settings_my_account);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.my_profile_update_account, container, false);
        ButterKnife.inject(this, view);
        mUpdateButton.setOnClickListener(this);
        CredentialsDTO credentials = mainCredentialsPreference.getCredentials();
        if (credentials != null)
        {
            try
            {
                JSONObject jsonObject = credentials.createJSON();
                if (jsonObject != null)
                {
                    if (jsonObject.has("email"))
                    {
                        mEmail.setText(jsonObject.getString("email"));
                    }
                    else
                    {
                        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
                        if (userProfileDTO != null)
                        {
                            mEmail.setText(userProfileDTO.email);
                        }
                    }
                    if (jsonObject.has("password"))
                    {
                        mPassWord.setText(jsonObject.getString("password"));
                        mSavedPassword = mPassWord.getText().toString();
                    }
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return view;
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_login:
                if (!checkEmailAndPassword())
                {
                    return ;
                }
                UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
                if (userProfileDTO != null)
                {
                    if (!userProfileDTO.email.contentEquals(mEmail.getText())
                            || !mSavedPassword.contentEquals(mPassWord.getText()))
                    {
                        progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.updating);
                        UserFormDTO userFormDTO = createForm();
                        detachMiddleCallbackUpdateUserProfile();
                        middleCallbackUpdateUserProfile = userServiceWrapper.get().updateAccount(
                                currentUserId.toUserBaseKey(),
                                userFormDTO,
                                createUpdateUserProfileCallback());
                        return;
                    }
                }
                break;
        }
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }
    public UserFormDTO createForm()
    {
        UserFormDTO created = new UserFormDTO();
        created.email = mEmail.getText().toString();
        created.password = mPassWord.getText().toString();
        created.passwordConfirmation = mPassWord.getText().toString();
        return created;
    }

    private void detachMiddleCallbackUpdateUserProfile()
    {
        if (middleCallbackUpdateUserProfile != null)
        {
            middleCallbackUpdateUserProfile.setPrimaryCallback(null);
        }
        middleCallbackUpdateUserProfile = null;
    }

    private THCallback<UserProfileDTO> createUpdateUserProfileCallback()
    {
        return new THCallback<UserProfileDTO>()
        {
            @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
            {
                progressDialogUtil.dismiss(getActivity());
                userProfileCache.put(currentUserId.toUserBaseKey(), userProfileDTO);
                THToast.show(R.string.settings_update_profile_successful);
                mainCredentialsPreference.setCredentials(
                        new EmailCredentialsDTO(mEmail.getText().toString(), mPassWord.getText().toString()));
                popCurrentFragment();
            }

            @Override protected void failure(THException ex)
            {
                progressDialogUtil.dismiss(getActivity());
                THToast.show(ex.getMessage());
                popCurrentFragment();
            }
        };
    }

    private boolean checkEmailAndPassword()
    {
        if (mEmail.getText().toString().isEmpty())
        {
            THToast.show(R.string.register_error_account);
            return false;
        }
        else if (mPassWord.getText().length() < 6)
        {
            THToast.show(R.string.register_error_password);
            return false;
        }
        return true;
    }
}
