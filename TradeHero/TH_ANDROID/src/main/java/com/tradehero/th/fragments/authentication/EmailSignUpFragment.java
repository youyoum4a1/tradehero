package com.tradehero.th.fragments.authentication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.picasso.Picasso;
import com.tradehero.common.utils.FileUtils;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.AuthenticationActivity;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.settings.ProfileInfoView;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.BitmapTypedOutput;
import com.tradehero.th.models.graphics.BitmapTypedOutputFactory;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.BitmapForProfileFactory;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Register using email.
 */
public class EmailSignUpFragment extends EmailSignInOrUpFragment implements View.OnClickListener {
    //java.lang.IllegalArgumentException: Can only use lower 16 bits for requestCode
    private static final int REQUEST_GALLERY = new Random(new Date().getTime()).nextInt(Short.MAX_VALUE);
    private static final int REQUEST_CAMERA = new Random(new Date().getTime() + 1).nextInt(Short.MAX_VALUE);

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
    private String newImagePath;
    private MiddleCallback<Response> sendCodeMiddleCallback;
    protected boolean mIsPhoneNumRegister;

    @Inject Analytics analytics;
    @Inject BitmapForProfileFactory bitmapForProfileFactory;
    @Inject BitmapTypedOutputFactory bitmapTypedOutputFactory;
    @Inject Picasso picasso;
    @Inject UserServiceWrapper userServiceWrapper;

    //Camera
    private Bitmap photo;

    private static long last_time_request_verify_code = -1;
    private final long duration_verify_code = 60;
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

        DaggerUtils.inject(this);
        analytics.tagScreen(AnalyticsConstants.Register_Form);
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.RegisterFormScreen));
        analytics.addEvent(new MethodEvent(AnalyticsConstants.SignUp_Tap, AnalyticsConstants.Email));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.authentication_register);
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
                if (charSequence.length() == 11) {
                    Pattern p = Pattern.compile("[0-9]*");
                    Matcher m = p.matcher(charSequence);
                    if (m.matches()) {
                        mIsPhoneNumRegister = true;
                    }
                }
                if (verifyCodeLayout != null) {
                    verifyCodeLayout.setVisibility(mIsPhoneNumRegister ? View.VISIBLE : View.GONE);
                }
                int lines = emailEditText.getLineCount();
                if (lines > 1) {
                    String str = charSequence.toString();
                    int cursorStart = emailEditText.getSelectionStart();
                    int cursorEnd = emailEditText.getSelectionEnd();
                    if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) {
                        str = str.substring(0, cursorStart-1) + str.substring(cursorStart);
                    } else {
                        str = str.substring(0, emailEditText.length()-1);
                    }
                    emailEditText.setText(str);
                    emailEditText.setSelection(emailEditText.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordEditText = (EditText) view.findViewById(R.id.authentication_sign_up_password);
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                int lines = passwordEditText.getLineCount();
                if (lines > 1) {
                    String str = charSequence.toString();
                    int cursorStart = passwordEditText.getSelectionStart();
                    int cursorEnd = passwordEditText.getSelectionEnd();
                    if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) {
                        str = str.substring(0, cursorStart-1) + str.substring(cursorStart);
                    } else {
                        str = str.substring(0, passwordEditText.length()-1);
                    }
                    passwordEditText.setText(str);
                    passwordEditText.setSelection(passwordEditText.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        verifyCodeLayout = (RelativeLayout) view.findViewById(R.id.login_verify_code_layout);
        verifyCode = (EditText) view.findViewById(R.id.verify_code);
        verifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                int lines = verifyCode.getLineCount();
                if (lines > 1) {
                    String str = charSequence.toString();
                    int cursorStart = verifyCode.getSelectionStart();
                    int cursorEnd = verifyCode.getSelectionEnd();
                    if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) {
                        str = str.substring(0, cursorStart-1) + str.substring(cursorStart);
                    } else {
                        str = str.substring(0, verifyCode.length()-1);
                    }
                    verifyCode.setText(str);
                    verifyCode.setSelection(verifyCode.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        getVerifyCodeButton = (TextView) view.findViewById(R.id.get_verify_code_button);
        getVerifyCodeButton.setOnClickListener(this);
        requestVerifyCodeStr = getActivity().getResources().getString(R.string.login_get_verify_code);
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
                int lines = mDisplayName.getLineCount();
                if (lines > 1) {
                    String str = charSequence.toString();
                    int cursorStart = mDisplayName.getSelectionStart();
                    int cursorEnd = mDisplayName.getSelectionEnd();
                    if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) {
                        str = str.substring(0, cursorStart-1) + str.substring(cursorStart);
                    } else {
                        str = str.substring(0, mDisplayName.length()-1);
                    }
                    mDisplayName.setText(str);
                    mDisplayName.setSelection(mDisplayName.getText().length());
                }
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

        this.mServiceText = (TextView) view.findViewById(R.id.txt_term_of_service_signin);
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
                    ((NavigatorActivity) getActivity()).getNavigator().popFragment();
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

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY && data != null) {
                try {
                    handleDataFromLibrary(data);
                } catch (OutOfMemoryError e) {
                    THToast.show(R.string.error_decode_image_memory);
                } catch (Exception e) {
                    THToast.show(R.string.error_fetch_image_library);
                    Timber.e(e, "Failed to extract image from library");
                }
                return;
            }
            if(requestCode == REQUEST_CAMERA && data != null){
                if(data.getData()!=null){
                    try {
                        handleDataFromLibrary(data);
                    } catch (OutOfMemoryError e) {
                        THToast.show(R.string.error_decode_image_memory);
                    } catch (Exception e) {
                        THToast.show(R.string.error_fetch_image_library);
                        Timber.e(e, "Failed to extract image from library");
                    }
                }else{
                    newImagePath = "";
                    Bundle bundle = data.getExtras();
                    if(bundle != null){
                        photo = (Bitmap) bundle.get("data");
                        if(photo!=null){
                            mPhoto.setImageBitmap(photo);
                        }
                    }
                }
                return;
            }
            if (requestCode == REQUEST_GALLERY) {
                Timber.e(new Exception("Got null data from library"), "");
                return;
            }
        }
        if (resultCode != Activity.RESULT_CANCELED) {
            Timber.e(new Exception("Failed to get image from libray, resultCode: " + resultCode), "");
            return;
        }
    }

    public void handleDataFromLibrary(Intent data) {
        Uri selectedImageUri = data.getData();
        if (selectedImageUri != null) {
            String selectedPath = FileUtils.getPath(getActivity(), selectedImageUri);
            setNewImagePath(selectedPath);
        } else {
            alertDialogUtil.popWithNegativeButton(getActivity(),
                    R.string.error_fetch_image_library,
                    R.string.error_fetch_image_library,
                    R.string.cancel);
        }
    }

    public void setNewImagePath(String newImagePath) {
        this.newImagePath = newImagePath;
        displayProfileImage();
    }

    public void displayProfileImage() {
        if (newImagePath != null) {
            Bitmap decoded = bitmapForProfileFactory.decodeBitmapForProfile(getResources(), newImagePath);
            if (decoded != null) {
                mPhoto.setImageBitmap(decoded);
                return;
            }
        }
        displayDefaultProfileImage();
    }

    public void displayDefaultProfileImage() {
        if (this.mPhoto != null && picasso != null) {
            picasso.load(R.drawable.superman_facebook)
                    //.transform(userPhotoTransformation)
                    .into(mPhoto);
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
        //return this.profileView.areFieldsValid();
        //if (mDisplayName.getText().toString().isEmpty())
        //{
        //    THToast.show(R.string.register_error_displayname);
        //    return false;
        //}
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
        if(!isValidEmail(emailEditText.getText())&&!isValidPhoneNumber(emailEditText.getText())){
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
        if (newImagePath != null) {
            map.put(UserFormFactory.KEY_PROFILE_PICTURE, safeCreateProfilePhoto());
        }
    }

    protected BitmapTypedOutput safeCreateProfilePhoto() {
        BitmapTypedOutput created = null;
        if (!TextUtils.isEmpty(newImagePath)) {
            try {
                created = bitmapTypedOutputFactory.createForProfilePhoto(
                        getResources(), bitmapForProfileFactory, newImagePath);
            } catch (OutOfMemoryError e) {
                THToast.show(R.string.error_decode_image_memory);
            }
        }else {
            if(photo==null){
                return null;
            }
            created = new BitmapTypedOutput(BitmapTypedOutput.TYPE_JPEG, photo, String.valueOf(System.currentTimeMillis()), 75);
        }
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
            ((AuthenticationActivity) getActivity()).onBackPressed();
        }
    }

    protected void askImageFromLibrary() {
        Intent libraryIntent = new Intent(Intent.ACTION_PICK);
        libraryIntent.setType("image/jpeg");
        startActivityForResult(libraryIntent, REQUEST_GALLERY);
    }

    protected void askImageFromCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public AuthenticationMode getAuthenticationMode() {
        return AuthenticationMode.SignUpWithEmail;
    }

    protected ProfileInfoView.Listener createProfileViewListener() {
        return new EmailSignUpProfileViewListener();
    }

    protected class EmailSignUpProfileViewListener implements ProfileInfoView.Listener {
        @Override
        public void onUpdateRequested() {

        }

        @Override
        public void onImageFromCameraRequested() {
            askImageFromCamera();
        }

        @Override
        public void onImageFromLibraryRequested() {
            askImageFromLibrary();
        }
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

    private boolean isValidPhoneNumber(CharSequence charSequence) {
        boolean isValid = false;
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(charSequence);
        if (m.matches()) {
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }

    private boolean isValidEmail(CharSequence charSequence) {
        boolean isValid = false;
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(charSequence);
        if (m.matches()) {
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }


}



