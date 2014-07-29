package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UpdateReferralCodeDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SettingsReferralCodeFragment extends DashboardFragment implements View.OnClickListener
{
    private MiddleCallback<Response> middleCallback;
    private ProgressDialog progressDialog;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtilLazy;
    @Inject Lazy<CurrentActivityHolder> currentActivityHolderLazy;

    @InjectView(R.id.btn_ok) Button mOKButton;
    @InjectView(R.id.btn_cancel) Button mCancelButton;
    @InjectView(R.id.referral_code) EditText mInviteCode;
    @InjectView(R.id.settings_referral_code) TextView mReferralCode;
    @InjectView(R.id.referral_code_dialog_key) LinearLayout mReferralCodeLayout;
    @InjectView(R.id.already_done_key) LinearLayout mAlreadyDoneLayout;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings_referral_code_layout, container, false);
        ButterKnife.inject(this, view);
        mOKButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            mReferralCode.setText(userProfileDTO.referralCode);
            mInviteCode.setText(userProfileDTO.inviteCode);
            if (userProfileDTO.inviteCode != null)
            {
                mReferralCodeLayout.setVisibility(View.GONE);
                mAlreadyDoneLayout.setVisibility(View.VISIBLE);
            }
        }
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        detachMiddleCallback();
        getProgressDialog().dismiss();
        mOKButton.setOnClickListener(null);
        mCancelButton.setOnClickListener(null);
        super.onDestroy();
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_ok:
                if (mInviteCode.getText().toString().length() > 5)
                {
                    detachMiddleCallback();
                    getProgressDialog().show();
                    UpdateReferralCodeDTO updateReferralCodeDTO = new UpdateReferralCodeDTO(
                            mInviteCode.getText().toString());
                    middleCallback = userServiceWrapperLazy.get().updateReferralCode(currentUserId.toUserBaseKey(), updateReferralCodeDTO, new TrackCallback());
                }
                break;
            case R.id.btn_cancel:
                getDashboardNavigator().popFragment();
                break;
        }
    }

    private class TrackCallback implements retrofit.Callback<Response>
    {
        @Override public void success(Response response, Response response2)
        {
            userProfileCache.invalidate(currentUserId.toUserBaseKey());
            getProgressDialog().dismiss();
            mReferralCodeLayout.setVisibility(View.GONE);
            mAlreadyDoneLayout.setVisibility(View.VISIBLE);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            getProgressDialog().dismiss();
        }
    }

    private void detachMiddleCallback()
    {
        if (middleCallback != null)
        {
            middleCallback.setPrimaryCallback(null);
        }
        middleCallback = null;
    }

    private ProgressDialog getProgressDialog()
    {
        if (progressDialog != null)
        {
            return progressDialog;
        }
        progressDialog = progressDialogUtilLazy.get().show(currentActivityHolderLazy.get().getCurrentActivity(), R.string.loading_loading,
                R.string.alert_dialog_please_wait);
        progressDialog.hide();
        return progressDialog;
    }
}
