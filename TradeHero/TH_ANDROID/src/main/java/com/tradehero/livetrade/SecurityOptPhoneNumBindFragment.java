package com.tradehero.livetrade;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.password.PhoneNumberBindDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class SecurityOptPhoneNumBindFragment extends DashboardFragment implements View.OnClickListener {

    public static final String INTENT_REFRESH_COMPETITION_DISCUSSIONS = "SecurityOptPhoneNumBindFragment.success";

    @Inject UserServiceWrapper userServiceWrapper;

    @InjectView(R.id.phone_number)
    EditText phoneNumber;
    @InjectView(R.id.verify_code)
    EditText verifyCode;
    @InjectView(R.id.get_verify_code_button)
    TextView getVerifyCodeButton;
    @InjectView(R.id.btn_next)
    Button btnNext;

    public static long last_time_request_verify_code = -1;
    public final static long duration_verify_code = 60;
    private String requestVerifyCodeStr = "";
    private Runnable refreshVerifyCodeRunnable = new Runnable() {
        @Override
        public void run() {
            if (getVerifyCodeButton == null) {
                return;
            }
            long limitTime = (System.currentTimeMillis() - last_time_request_verify_code) / 1000;
            if (limitTime < duration_verify_code && limitTime > 0) {
                getVerifyCodeButton.setClickable(false);
                getVerifyCodeButton.setText(String.valueOf(duration_verify_code - limitTime));
                getVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng_again);
                Handler handler = new Handler();
                handler.postDelayed(refreshVerifyCodeRunnable, 1000);
            } else {
                getVerifyCodeButton.setClickable(true);
                getVerifyCodeButton.setText(requestVerifyCodeStr);
                getVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng);
                last_time_request_verify_code = -1;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phone_number_bind_up, container, false);

        ButterKnife.inject(this, view);
        requestVerifyCodeStr = getString(R.string.login_get_verify_code);

        getVerifyCodeButton.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.get_verify_code_button:
                getVerifyCodeButton.setClickable(false);
                getVerifyCodeButton.setText(String.valueOf(duration_verify_code));
                getVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng_again);
                last_time_request_verify_code = System.currentTimeMillis();
                Handler handler = new Handler();
                handler.postDelayed(refreshVerifyCodeRunnable, 1000);

                userServiceWrapper.sendCode(phoneNumber.getText().toString(), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        // Do nothing.
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        THToast.show(new THException(error));
                        last_time_request_verify_code = -1;
                    }
                });
                break;
            case R.id.btn_next:
                userServiceWrapper.phoneNumBind(phoneNumber.getText().toString(), verifyCode.getText().toString(), new Callback<PhoneNumberBindDTO>() {
                    @Override
                    public void success(PhoneNumberBindDTO phoneNumberBindDTO, Response response) {
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(INTENT_REFRESH_COMPETITION_DISCUSSIONS));
                        popCurrentFragment();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        THToast.show(new THException(error));
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(INTENT_REFRESH_COMPETITION_DISCUSSIONS));
                        popCurrentFragment();
                    }
                });

                break;
        }
    }
}
