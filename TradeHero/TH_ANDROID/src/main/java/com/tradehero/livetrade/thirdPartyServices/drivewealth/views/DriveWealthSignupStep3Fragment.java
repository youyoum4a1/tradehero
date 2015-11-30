package com.tradehero.livetrade.thirdPartyServices.drivewealth.views;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.tradehero.common.utils.THToast;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthManager;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class DriveWealthSignupStep3Fragment extends DashboardFragment {

    @Inject DriveWealthManager mDriveWealthManager;

    @InjectView(R.id.email)
    EditText email;
    @InjectView(R.id.nickname)
    EditText nickname;
    @InjectView(R.id.password1)
    EditText password1;
    @InjectView(R.id.password2)
    EditText password2;
    @InjectView(R.id.btn_next)
    Button btnNext;
    @InjectView(R.id.error_msg)
    TextView mErrorMsgText;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        setHeadViewMiddleMain("登陆信息(3/7)");
        setHeadViewRight0(getString(R.string.cancel));
    }

    @Override
    public void onClickHeadRight0() {
        getActivity().finish();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dw_signup_page3, container, false);
        ButterKnife.inject(this, view);
        initView();
        DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
        if (formDTO.email != null) {
            email.setText(formDTO.email);
        }

        if (formDTO.userName != null) {
            nickname.setText(formDTO.userName);
        }

        if (formDTO.password != null) {
            password1.setText(formDTO.password);
            password2.setText(formDTO.password);
        }

        checkNEnableNextButton();
        return view;
    }

    private void initView() {
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!isEmail(email.getText().toString())) {
                        THToast.show(R.string.email_error);
                    }
                }
            }
        });
        password1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (password1.getText().toString().length() < 8
                            || password1.getText().toString().length() > 20) {
                        THToast.show(R.string.password_length_error);
                    }
                }
            }
        });
        password2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (password2.getText().toString().length() < 8 || password2.getText().toString().length() > 20) {
                        THToast.show(R.string.password_length_error);
                    } else if (!password1.getText().toString().equalsIgnoreCase(password2.getText().toString())) {
                        THToast.show(R.string.password_not_same_error);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.btn_next)
    public void onNextClick() {
        if (!checkInfo()) {
            return;
        }
            DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
            formDTO.email = email.getText().toString();
            formDTO.userName = nickname.getText().toString();
            formDTO.password = password1.getText().toString();

            pushFragment(DriveWealthSignupStep4Fragment.class, new Bundle());
    }

    private boolean checkInfo() {
        if (!isEmail(email.getText().toString())) {
            mErrorMsgText.setVisibility(View.VISIBLE);
            mErrorMsgText.setText(R.string.email_error);
            return false;
        }
        if (password1.getText().toString().isEmpty() || password1.getText().toString().length() < 8
                || password1.getText().toString().length() > 20 ||
                !password1.getText().toString().equalsIgnoreCase(password2.getText().toString())) {
            mErrorMsgText.setVisibility(View.VISIBLE);
            mErrorMsgText.setText(R.string.password_length_error);
            return false;
        }
        mErrorMsgText.setVisibility(View.GONE);
        return true;
    }

    private boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    @OnTextChanged({R.id.email, R.id.nickname, R.id.password1, R.id.password2})
    public void onEditTextChanged(CharSequence text) {
        checkNEnableNextButton();
    }

    private void checkNEnableNextButton() {
        if (email.getText().length() > 0 && nickname.getText().length() > 0 &&
                password1.getText().length() > 0 && password2.getText().length() > 0) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }
}
