package com.tradehero.livetrade.thirdPartyServices.drivewealth.views;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tradehero.common.utils.THToast;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthManager;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthServicesWrapper;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;
import com.tradehero.th.R;
import com.tradehero.th.api.users.password.BindBrokerDTO;
import com.tradehero.th.network.service.UserServiceWrapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class DriveWealthSignupStep7Fragment extends DriveWealthSignupBaseFragment {

    @Inject DriveWealthServicesWrapper mServices;
    @Inject DriveWealthManager mDriveWealthManager;
    @Inject UserServiceWrapper userServiceWrapper;

    @InjectView(R.id.agreement1)
    CheckBox agreement1;
    @InjectView(R.id.agreement2)
    CheckBox agreement2;
    @InjectView(R.id.agreeAll)
    CheckBox agreeAll;
    @InjectView(R.id.account_disclosures)
    TextView accountDisclosures;
    @InjectView(R.id.customer_account_agreement)
    TextView customerAccountAgreement;
    @InjectView(R.id.bats_subscriber_agreement)
    TextView batsSubscriberAgreement;
    @InjectView(R.id.signature)
    EditText signature;
    @InjectView(R.id.error_msg)
    TextView mErrorMsgText;
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
                    THToast.show("开户提交成功，在三个工作日之内我们将完成审核工作！");
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

    @Override
    public String getTitle() {
        return "提交申请(7/7)";
    }

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

    @OnClick({R.id.account_disclosures, R.id.customer_account_agreement, R.id.bats_subscriber_agreement})
    public void onDocumentClicked(View view) {
        Intent browserIntent;
        switch (view.getId()) {
            case R.id.account_disclosures:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://drivewealth.com/zh-hans/account-disclosures/"));
                startActivity(browserIntent);
                break;

            case R.id.customer_account_agreement:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://drivewealth.com/zh-hans/customer-account-agreement/"));
                startActivity(browserIntent);
                break;

            case R.id.bats_subscriber_agreement:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://drivewealth.com/bats-subscriber-agreement/"));
                startActivity(browserIntent);
                break;
        }
    }

    @OnClick(R.id.btn_next)
    public void onNextClick() {
        DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
        if (!isChinese(signature.getText().toString())) {
            mErrorMsgText.setVisibility(View.VISIBLE);
            mErrorMsgText.setText(R.string.name_error);
            return;
        } else if (!signature.getText().toString().equals(formDTO.lastName + formDTO.firstName)) {
            mErrorMsgText.setVisibility(View.VISIBLE);
            mErrorMsgText.setText(R.string.name_signature_mismatch);
            return;
        } else {
            mErrorMsgText.setVisibility(View.GONE);
        }

        formDTO.ackSignedBy = signature.getText().toString();

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
        } else {
            mProgressDialog.dismiss();
        }
        mProgressDialog.setMessage(getString(R.string.dw_signup_in_progress));
        mProgressDialog.show();

        mDriveWealthManager.storeSignupInfo(getActivity());
        mServices.processSignupLive(getActivity());
    }

    private boolean isChinese(String text) {
        for (int i = 0; i < text.length(); i++) {
            Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
            Matcher m = p.matcher(String.valueOf(text.charAt(i)));
            if (!m.matches() && !String.valueOf(text.charAt(i)).matches(" ")) {
                return false;
            }
        }
        return true;
    }

    @OnTextChanged(R.id.signature)
    public void onEditTextChanged(CharSequence text) {
        checkNEnableNextButton();
    }

    @OnCheckedChanged({R.id.agreement1, R.id.agreement2, R.id.agreeAll})
    public void onCheckChanged() {
        checkNEnableNextButton();
    }

    private void checkNEnableNextButton() {
        if (signature.getText().length() > 0 &&
                agreement1.isChecked() && agreement2.isChecked() && agreeAll.isChecked()) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }
}
