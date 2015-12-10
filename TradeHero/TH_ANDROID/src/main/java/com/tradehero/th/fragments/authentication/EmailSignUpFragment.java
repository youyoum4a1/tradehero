package com.tradehero.th.fragments.authentication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.BitmapTypedOutput;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.EmailSignUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Register using email or phone number.
 */
public class EmailSignUpFragment extends EmailSignInOrUpFragment implements View.OnClickListener {

    //Photo Request Code
    private static final int REQUEST_GALLERY = 299;
    private static final int REQUEST_CAMERA = 399;
    private static final int REQUEST_PHOTO_ZOOM = 199;

    protected ViewSwitcher mSwitcher;
    protected EditText emailEditText;
    protected EditText passwordEditText;
    protected RelativeLayout verifyCodeLayout;
    protected EditText verifyCode;
    protected TextView getVerifyCodeButton;
    protected EditText mDisplayName;
    protected Button mNextButton;
    private ImageView backButton;
    protected ImageView mPhoto;
    protected TextView mServiceText;
    protected ImageView mAgreeButton;
    protected LinearLayout mAgreeLayout;
    private MiddleCallback<Response> sendCodeMiddleCallback;
    protected boolean mIsPhoneNumRegister;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject Analytics analytics;

    //Camera
    private Bitmap photo;
    private File file;

    //Verify Code
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        analytics.addEvent(new MethodEvent(AnalyticsConstants.SIGN_UP, AnalyticsConstants.BUTTON_LOGIN_REGISTER));
        DaggerUtils.inject(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.authentication_register);
        setHeadViewRight0Visibility(View.INVISIBLE);
    }

    @Override
    public int getDefaultViewId() {
        return R.layout.authentication_email_sign_up;
    }

    @Override
    protected void initSetup(View view) {
        this.emailEditText = (EditText) view.findViewById(R.id.authentication_sign_up_email);
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mIsPhoneNumRegister = false;
                if (EmailSignUtils.isPhoneNumber(charSequence)) {
                    mIsPhoneNumRegister = true;
                }
                if (verifyCodeLayout != null) {
                    verifyCodeLayout.setVisibility(mIsPhoneNumRegister ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordEditText = (EditText) view.findViewById(R.id.authentication_sign_up_password);

        verifyCodeLayout = (RelativeLayout) view.findViewById(R.id.login_verify_code_layout);
        verifyCode = (EditText) view.findViewById(R.id.verify_code);

        getVerifyCodeButton = (TextView) view.findViewById(R.id.get_verify_code_button);
        getVerifyCodeButton.setOnClickListener(this);
        requestVerifyCodeStr = getString(R.string.login_get_verify_code);
        long limitTime = (System.currentTimeMillis() - last_time_request_verify_code) / 1000;
        if (limitTime < duration_verify_code && limitTime > 0) {
            getVerifyCodeButton.setClickable(false);
            getVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng_again);
            getVerifyCodeButton.setText(String.valueOf(duration_verify_code - limitTime));
            Handler handler = new Handler();
            handler.postDelayed(refreshVerifyCodeRunnable, 1000);
        } else {
            getVerifyCodeButton.setClickable(true);
            getVerifyCodeButton.setText(requestVerifyCodeStr);
            getVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng);
            last_time_request_verify_code = -1;
        }


        mDisplayName = (EditText) view.findViewById(R.id.authentication_sign_up_username);
        mDisplayName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (charSequence.length() > 1) {
                    signButton.setBackgroundResource(R.drawable.basic_red_selector_round_corner);
                    signButton.setEnabled(charSequence.length() > 1);
                } else {
                    signButton.setEnabled(false);
                    signButton.setBackgroundResource(R.drawable.yijian);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        this.mPhoto = (ImageView) view.findViewById(R.id.image_optional);
        mPhoto.setOnClickListener(this);

        this.mAgreeButton = (ImageView) view.findViewById(R.id.authentication_agree);

        this.mAgreeLayout = (LinearLayout) view.findViewById(R.id.authentication_agreement);
        mAgreeLayout.setOnClickListener(this);

        this.mServiceText = (TextView) view.findViewById(R.id.txt_term_of_service);
        mServiceText.setOnClickListener(onClickListener);

        this.signButton = (Button) view.findViewById(R.id.authentication_sign_up_button);
        this.signButton.setOnClickListener(this);
        signButton.setEnabled(false);

        mNextButton = (Button) view.findViewById(R.id.btn_next);
        mNextButton.setOnClickListener(this);
        mSwitcher = (ViewSwitcher) view.findViewById(R.id.switcher);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DeviceUtil.showKeyboardDelayed(emailEditText);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getActivity() instanceof DashboardNavigatorActivity) {
                    ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator().popFragment();
                } else {
                    Timber.e("Activity is not a DashboardNavigatorActivity", new Exception());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_GALLERY && data != null) {
            if (data.getData() != null) {
                startPhotoZoom(data.getData(), 150);
            }
            return;
        }
        if (requestCode == REQUEST_CAMERA) {
            startPhotoZoom(Uri.fromFile(file), 150);
            return;
        }
        if (requestCode == REQUEST_PHOTO_ZOOM && data != null) {
            storeAndDisplayPhoto(data);
        }
    }

    @Override
    public void onDestroyView() {
        detachSendCodeMiddleCallback();
        if (this.signButton != null) {
            this.signButton.setOnClickListener(null);
        }
        this.signButton = null;
        if (backButton != null) {
            backButton.setOnClickListener(null);
            backButton = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.authentication_sign_up_button:
                //clear old user info
                THUser.clearCurrentUser();
                handleSignInOrUpButtonClicked(view);
                break;
            case R.id.image_optional:
                showChooseImageDialog();
                break;
            case R.id.btn_next:
                if (checkAccountAndPassword()) {
                    mSwitcher.setDisplayedChild(1);
                }
                break;
            case R.id.authentication_agreement:
                mNextButton.setEnabled(!mNextButton.isEnabled());
                mAgreeButton.setImageResource(mNextButton.isEnabled() ?
                        R.drawable.register_duihao : R.drawable.register_duihao_cancel);
                break;
            case R.id.get_verify_code_button:
                requestVerifyCode();
                break;
        }
    }

    private void requestVerifyCode() {
        getVerifyCodeButton.setClickable(false);
        getVerifyCodeButton.setText(String.valueOf(duration_verify_code));
        getVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng_again);
        last_time_request_verify_code = System.currentTimeMillis();
        Handler handler = new Handler();
        handler.postDelayed(refreshVerifyCodeRunnable, 1000);
        detachSendCodeMiddleCallback();
        sendCodeMiddleCallback = userServiceWrapper.sendCode(emailEditText.getText().toString(), new SendCodeCallback());
    }

    private void showChooseImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setItems(R.array.register_choose_image, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        askImageFromCamera();
                        break;
                    case 1:
                        askImageFromLibrary();
                        break;
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }

    @Override
    protected void forceValidateFields() {
        //this.profileView.forceValidateFields();
    }

    @Override
    public boolean areFieldsValid() {
        String displayNameStr = mDisplayName.getText().toString();
        if (displayNameStr.contains(" ")) {
            THToast.show(R.string.sign_in_display_name_no_blank);
            return false;
        }
        return true;
    }

    private boolean checkAccountAndPassword() {
        if (emailEditText.getText().toString().isEmpty()) {
            THToast.show(R.string.register_error_account);
            return false;
        }
        if (passwordEditText.getText().length() < 6) {
            THToast.show(R.string.register_error_password);
            return false;
        }
        if (!EmailSignUtils.isValidEmail(emailEditText.getText()) && !isValidPhoneNumber(emailEditText.getText())) {
            THToast.show(R.string.enter_phone_email_error);
            return false;
        }
        return true;
    }

    @Override
    protected Map<String, Object> getUserFormMap() {
        Map<String, Object> map = super.getUserFormMap();
        populateUserFormMap(map);
        return map;
    }

    public void populateUserFormMap(Map<String, Object> map) {
        if (mIsPhoneNumRegister) {
            populateUserFormMapFromEditable(map, UserFormFactory.KEY_PHONE_NUMBER, emailEditText.getText());
            populateUserFormMapFromEditable(map, UserFormFactory.KEY_VERIFY_CODE, verifyCode.getText());
        }
        populateUserFormMapFromEditable(map, UserFormFactory.KEY_EMAIL, emailEditText.getText());
        populateUserFormMapFromEditable(map, UserFormFactory.KEY_PASSWORD, passwordEditText.getText());
        populateUserFormMapFromEditable(map, UserFormFactory.KEY_PASSWORD_CONFIRM, passwordEditText.getText());
        populateUserFormMapFromEditable(map, UserFormFactory.KEY_DISPLAY_NAME, mDisplayName.getText());
        populateUserFormMapFromEditable(map, UserFormFactory.KEY_INVITE_CODE, "");
        populateUserFormMapFromEditable(map, UserFormFactory.KEY_FIRST_NAME, "");
        populateUserFormMapFromEditable(map, UserFormFactory.KEY_LAST_NAME, "");
        map.put(UserFormFactory.KEY_PROFILE_PICTURE, safeCreateProfilePhoto());
    }

    protected BitmapTypedOutput safeCreateProfilePhoto() {
        BitmapTypedOutput created = null;
        if (photo == null) {
            return null;
        }
        created = new BitmapTypedOutput(BitmapTypedOutput.TYPE_JPEG, photo, String.valueOf(System.currentTimeMillis()), 75);
        return created;
    }

    private void populateUserFormMapFromEditable(Map<String, Object> toFill, String key, Editable toPick) {
        if (toPick != null) {
            toFill.put(key, toPick.toString());
        }
    }

    private void populateUserFormMapFromEditable(Map<String, Object> toFill, String key, String toPick) {
        if (toPick != null) {
            toFill.put(key, toPick);
        }
    }

    @Override
    public void onClickHeadLeft() {
        if (mSwitcher.getDisplayedChild() == 1) {
            mSwitcher.setDisplayedChild(0);
        } else {
            getActivity().onBackPressed();
        }
    }

    protected void askImageFromLibrary() {
        Intent libraryIntent = new Intent(Intent.ACTION_PICK);
        libraryIntent.setType("image/jpeg");
        startActivityForResult(libraryIntent, REQUEST_GALLERY);
    }

    protected void askImageFromCamera() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            THToast.show(R.string.photo_no_sdcard);
            return;
        }
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory(),
                "th_temp.jpg");
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    @Override
    public AuthenticationMode getAuthenticationMode() {
        return AuthenticationMode.SignUpWithEmail;
    }

    private class SendCodeCallback implements Callback<Response> {
        @Override
        public void success(Response response, Response response2) {
            THToast.show(R.string.send_verify_code_success);
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            THToast.show(new THException(retrofitError));
        }
    }

    private void detachSendCodeMiddleCallback() {
        if (sendCodeMiddleCallback != null) {
            sendCodeMiddleCallback.setPrimaryCallback(null);
        }
        sendCodeMiddleCallback = null;
    }

    private void startPhotoZoom(Uri data, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_PHOTO_ZOOM);
    }

    private void storeAndDisplayPhoto(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            photo = (Bitmap) bundle.get("data");
            if (photo != null) {
                mPhoto.setImageBitmap(photo);
            }
        }
    }

}



