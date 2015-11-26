package com.tradehero.livetrade.thirdPartyServices.drivewealth.views;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.tradehero.chinabuild.fragment.MyProfileFragment;
import com.tradehero.common.utils.THToast;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthManager;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthServicesWrapper;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;
import com.tradehero.th.R;
import com.tradehero.th.api.users.password.BindBrokerDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.service.UserServiceWrapper;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class DriveWealthSignupStep7Fragment extends DashboardFragment {

    @Inject DriveWealthServicesWrapper mServices;
    @Inject DriveWealthManager mDriveWealthManager;
    @Inject UserServiceWrapper userServiceWrapper;

    @InjectView(R.id.agreement1)
    CheckBox agreement1;
    @InjectView(R.id.agreement2)
    CheckBox agreement2;
    @InjectView(R.id.signature)
    EditText signature;
    @InjectView(R.id.btn_next)
    Button btnNext;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    private ProgressDialog mProgressDialog;

    private BroadcastReceiver mSuccessHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }

            DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
            userServiceWrapper.bindBroker(formDTO.phoneNumber, mDriveWealthManager.getUserID(), new Callback<BindBrokerDTO>() {
                @Override
                public void success(BindBrokerDTO bindBrokerDTO, Response response) {
                    getActivity().finish();
                }

                @Override
                public void failure(RetrofitError error) {
                    THToast.show(error.getLocalizedMessage());
                }
            });
        }
    };

    private BroadcastReceiver mFailedHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dw_signup_page7, container, false);
        ButterKnife.inject(this, view);

        DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
        if (formDTO.ackSignedBy != null) {
            signature.setText(formDTO.ackSignedBy);
        }
        progressBar.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(mSuccessHandler, new IntentFilter(DriveWealthServicesWrapper.DW_SIGNUP_SUCCESS));
        getActivity().registerReceiver(mFailedHandler, new IntentFilter(DriveWealthServicesWrapper.DW_SIGNUP_FAILED));
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(mSuccessHandler);
        getActivity().unregisterReceiver(mFailedHandler);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.btn_next)
    public void onNextClick() {
        final DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
        formDTO.ackSignedBy = signature.getText().toString();

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
        } else {
            mProgressDialog.dismiss();
        }
        mProgressDialog.setMessage(getString(R.string.dw_signup_in_progress));
        mProgressDialog.show();

        mServices.processSignupLive(getActivity());
    }

    @OnTextChanged(R.id.signature)
    public void onEditTextChanged(CharSequence text) {
        checkNEnableNextButton();
    }

    @OnCheckedChanged({R.id.agreement1, R.id.agreement2})
    public void onCheckChanged() {
        checkNEnableNextButton();
    }

    private void checkNEnableNextButton() {
        if (signature.getText().length() > 0 &&
                agreement1.isChecked() && agreement2.isChecked()) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }
}
