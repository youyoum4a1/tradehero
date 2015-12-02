package com.tradehero.livetrade.thirdPartyServices.drivewealth.views;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.tradehero.common.utils.THToast;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthManager;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthServicesWrapper;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthErrorDTO;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;
import com.tradehero.th.R;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class DriveWealthSignupStep3Fragment extends DriveWealthSignupBaseFragment {

    @Inject
    DriveWealthManager mDriveWealthManager;
    @InjectView(R.id.email)
    EditText email;
    @InjectView(R.id.nickname)
    EditText username;
    @InjectView(R.id.password_checkbox)
    CheckBox passwordCheckbox;
    @InjectView(R.id.showPassword)
    RelativeLayout showPassword;
    @InjectView(R.id.password1)
    EditText password1;
    @InjectView(R.id.confirm_checkbox)
    CheckBox confirmCheckbox;
    @InjectView(R.id.passwordOK)
    RelativeLayout passwordOK;
    @InjectView(R.id.password2)
    EditText password2;
    @InjectView(R.id.btn_next)
    Button btnNext;
    @InjectView(R.id.error_msg)
    TextView mErrorMsgText;
    @Inject DriveWealthServicesWrapper mDriveWealthServicesWrapper;
    private boolean mUserNameChecked = false;
    private ProgressDialog mProgressDialog;

    @Override
    public String getTitle() {
        return "登陆信息(3/7)";
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
            username.setText(formDTO.userName);
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
                if (!hasFocus && email != null) {
                    if (!isEmail(email.getText().toString())) {
                        THToast.show(R.string.email_error);
                    }
                }
            }
        });
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && username != null) {
                    if (!isUserName(username.getText().toString())) {
                        THToast.show(R.string.username_error);
                        mUserNameChecked = false;
                        checkNEnableNextButton();
                    } else {
                        if (mProgressDialog == null) {
                            mProgressDialog = new ProgressDialog(getActivity());
                        } else {
                            mProgressDialog.dismiss();
                        }
                        mProgressDialog.setMessage(getString(R.string.verifying_username));
                        mProgressDialog.show();

                        mDriveWealthServicesWrapper.checkUserName(username.getText().toString(), new Callback<DriveWealthErrorDTO>() {
                            @Override public void success(DriveWealthErrorDTO driveWealthErrorDTO, Response response) {
                                mProgressDialog.dismiss();
                                if (driveWealthErrorDTO.code == 200) {//"Username found [username=youyoum4a1]"
                                    THToast.show("昵称已存在");
//                                    THToast.show(driveWealthErrorDTO.message.replace("Username found", "交易昵称已被使用，请更改交易昵称"));
                                    mUserNameChecked = false;
                                } else {
                                    mUserNameChecked = true;
                                }
                                checkNEnableNextButton();
                            }

                            @Override public void failure(RetrofitError error) {
                                mProgressDialog.dismiss();
                                if (error.getResponse().getStatus() == 404) {//"Username not found [username=youyoum4a2]"
                                    mUserNameChecked = true;
                                }
                                checkNEnableNextButton();
                            }
                        });
                    }

                }
            }
        });
        password1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && password1 != null) {
                    if (password1.getText().toString().length() < 8
                            || password1.getText().toString().length() > 20) {
                        THToast.show(R.string.password_length_error);
                    }
                    if (!isPassword(password1.getText().toString())) {
                        THToast.show(R.string.login_password1_hint);
                    }
                }
            }
        });
        password2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && password2 != null && password1 != null) {
                    if (password2.getText().toString().length() < 8 || password2.getText().toString().length() > 20) {
                        THToast.show(R.string.password_length_error);
                    } else if (!password1.getText().toString().equalsIgnoreCase(password2.getText().toString())) {
                        THToast.show(R.string.password_not_same_error);
                    } else if (!isPassword(password2.getText().toString())) {
                        THToast.show(R.string.login_password1_hint);
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

    @OnClick({R.id.password_checkbox})
    public void showPasswordClick() {
        if (passwordCheckbox.isChecked()) {
            password1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            password1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    @OnClick(R.id.btn_next)
    public void onNextClick() {
        if (!checkInfo()) {
            return;
        }
        DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
        formDTO.email = email.getText().toString();
        formDTO.userName = username.getText().toString();
        formDTO.password = password1.getText().toString();

        pushFragment(DriveWealthSignupStep4Fragment.class, new Bundle());
    }

    private boolean checkInfo() {
        if (!isEmail(email.getText().toString())) {
            mErrorMsgText.setVisibility(View.VISIBLE);
            mErrorMsgText.setText(R.string.email_error);
            return false;
        }
        if (password2.getText().toString().length() < 8 || password2.getText().toString().length() > 20) {
            mErrorMsgText.setVisibility(View.VISIBLE);
            mErrorMsgText.setText(R.string.password_length_error);
            return false;
        } else if (!password1.getText().toString().equalsIgnoreCase(password2.getText().toString())) {
            mErrorMsgText.setVisibility(View.VISIBLE);
            mErrorMsgText.setText(R.string.password_not_same_error);
            return false;
        } else if (!isPassword(password2.getText().toString())) {
            mErrorMsgText.setVisibility(View.VISIBLE);
            mErrorMsgText.setText(R.string.login_password1_hint);
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

    private boolean isPassword(String text) {
        boolean isLetter = false, isDigit = false;
        for (int i = 0; i < text.length(); i++) {
            Pattern p = Pattern.compile("[0-9]*");
            Pattern p2 = Pattern.compile("[a-zA-Z]");
            Matcher m = p.matcher(String.valueOf(text.charAt(i)));
            Matcher m2 = p2.matcher(String.valueOf(text.charAt(i)));
            if (m.matches()) {
                isDigit = true;
            } else if (m2.matches()) {
                isLetter = true;
            } else {
                return false;
            }
        }
        return isLetter && isDigit;
    }

    private boolean isUserName(String text) {
        boolean isLetter = false, isDigit = false;
        for (int i = 0; i < text.length(); i++) {
            Pattern p = Pattern.compile("[0-9]*");
            Pattern p2 = Pattern.compile("[a-zA-Z]");
            Matcher m = p.matcher(String.valueOf(text.charAt(i)));
            Matcher m2 = p2.matcher(String.valueOf(text.charAt(i)));
            if (m.matches()) {
                isDigit = true;
            } else if (m2.matches()) {
                isLetter = true;
            } else {
                return false;
            }
        }
        return isLetter || isDigit;
    }

    @OnTextChanged({R.id.email})
    public void onEditTextChanged(CharSequence text) {
        checkNEnableNextButton();
    }

    @OnTextChanged({R.id.password1, R.id.password2})
    public void onPasswordConfirmChanged(CharSequence text) {
        if (isPassword(password1.getText().toString()) && password1.getText().toString().equals(password2.getText().toString())) {
            confirmCheckbox.setChecked(true);
        } else {
            confirmCheckbox.setChecked(false);
        }
        checkNEnableNextButton();
    }

    private void checkNEnableNextButton() {
        if (email.getText().length() > 0 && username.getText().length() > 0 &&
                password1.getText().length() > 0 && password2.getText().length() > 0 && mUserNameChecked) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }
}
