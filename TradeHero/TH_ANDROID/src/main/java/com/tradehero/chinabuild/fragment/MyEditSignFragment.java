package com.tradehero.chinabuild.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;

public class MyEditSignFragment extends DashboardFragment
{
    @InjectView(R.id.display_name) EditText mSignText;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    private MiddleCallback<UserProfileDTO> middleCallbackUpdateUserProfile;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(getString(R.string.settings_my_sign));
        setHeadViewRight0(getString(R.string.submit));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.setting_my_name_fragment_layout, container, false);
        ButterKnife.inject(this, view);
        setNeedToMonitorBackPressed(true);
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            if (userProfileDTO.signature != null && !userProfileDTO.signature.isEmpty()) {
                mSignText.setText(userProfileDTO.signature);
            }
        }
        return view;
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onClickHeadRight0()
    {
        String signText = mSignText.getText().toString();
        if(TextUtils.isEmpty(signText)){
            popCurrentFragment();
            return;
        }
        if(signText.contains(" ")){
            THToast.show(R.string.sign_in_display_name_no_blank);
            return;
        }
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            if (userProfileDTO.signature == null || userProfileDTO.signature.isEmpty() || !userProfileDTO.signature.contentEquals(mSignText.getText()))
            {
                progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.updating);
                UserFormDTO userFormDTO = createForm();
                detachMiddleCallbackUpdateUserProfile();
                middleCallbackUpdateUserProfile = userServiceWrapper.get().updateSign(
                        currentUserId.toUserBaseKey(),
                        userFormDTO,
                        createUpdateUserProfileCallback());
                return;
            }
        }
    }

    public UserFormDTO createForm()
    {
        UserFormDTO created = new UserFormDTO();
        created.signature = mSignText.getText().toString();
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

    @Override
    public void onBackPressed(){
        onClickHeadLeft();
    }

}
