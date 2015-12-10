package com.tradehero.livetrade.thirdPartyServices.drivewealth.views;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.tradehero.common.utils.THToast;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthManager;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;
import com.tradehero.th.R;
import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class DriveWealthSignupStep4Fragment extends DriveWealthSignupBaseFragment {

    @Inject DriveWealthManager mDriveWealthManager;

    @InjectView(R.id.lastName)
    EditText lastName;
    @InjectView(R.id.firstName)
    EditText firstName;
    @InjectView(R.id.lastNameEnglish) EditText lastNameEnglish;
    @InjectView(R.id.firstNameEnglish) EditText firstNameEnglish;
    @InjectView(R.id.birth_date) EditText mBirthDateEditText;
    @InjectView(R.id.gender) EditText mGenderEditText;
    @InjectView(R.id.marital) EditText mMaritalEditText;
    @InjectView(R.id.idNumber)
    EditText idNumber;
    @InjectView(R.id.address)
    EditText address;
    @InjectView(R.id.idcard_front)
    ImageView idcardFront;
    @InjectView(R.id.idcard_back)
    ImageView idcardBack;
    @InjectView(R.id.error_msg)
    TextView mErrorMsgText;
    @InjectView(R.id.btn_next)
    Button btnNext;

    private static final int REQUEST_GALLERY = 299;
    private static final int REQUEST_CAMERA = 399;
    private static final int REQUEST_PHOTO_ZOOM = 499;

    private int mLoadImageforId = 0;
    private File mFile;

    @Override
    public String getTitle() {
        return "个人资料(4/7)";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dw_signup_page4, container, false);
        ButterKnife.inject(this, view);
        initView();
        DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
        if (formDTO.firstName != null) {
            firstName.setText(formDTO.firstName);
        }

        if (formDTO.lastName != null) {
            lastName.setText(formDTO.lastName);
        }

        if (formDTO.firstNameInEng != null) {
            firstNameEnglish.setText(formDTO.firstNameInEng);
        }

        if (formDTO.lastNameInEng != null) {
            lastNameEnglish.setText(formDTO.lastNameInEng);
        }

        if (formDTO.dob != null) {
            mBirthDateEditText.setText(formDTO.dob);
        }
        if (formDTO.gender != null) {
            mGenderEditText.setText(formDTO.gender);
        }
        if (formDTO.martialStatus != null) {
            mMaritalEditText.setText(formDTO.martialStatus);
        }

        if (formDTO.idNO != null) {
            idNumber.setText(formDTO.idNO);
        }

        if (formDTO.address != null) {
            address.setText(formDTO.address);
        }

        if (formDTO.idcardFront != null) {
            idcardFront.setImageURI(formDTO.idcardFront);
        }

        if (formDTO.idcardBack != null) {
            idcardBack.setImageURI(formDTO.idcardBack);
        }

        checkNEnableNextButton();

        return view;
    }

    private void initView() {
        firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (firstName != null && firstName.getText() == null || !isChinese(firstName.getText().toString())) {
                        THToast.show(R.string.name_error);
                    }
                }
            }
        });
        lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (lastName != null && lastName.getText() == null || !isChinese(lastName.getText().toString())) {
                        THToast.show(R.string.name_error);
                    }
                }
            }
        });

        mBirthDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (mBirthDateEditText != null && mBirthDateEditText.getText() == null || !isDate(mBirthDateEditText.getText().toString())) {
                        THToast.show(R.string.date_error);
                    }
                }
            }
        });
        mGenderEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (mGenderEditText != null && mGenderEditText.getText() == null || !isGender(mGenderEditText.getText().toString())) {
                        THToast.show(R.string.gender_error);
                    }
                }
            }
        });
        mMaritalEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (mMaritalEditText != null && mMaritalEditText.getText() == null || !isMarital(mMaritalEditText.getText().toString())) {
                        THToast.show(R.string.marital_error);
                    }
                }
            }
        });

        idNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (idNumber != null && idNumber.getText() == null || idNumber.getText().toString().isEmpty() || idNumber.getText().toString().length() < 18) {
                        THToast.show(R.string.id_number_hint);
                    }
                }
            }
        });
    }

    private boolean isMarital(String s) {
        if (s.matches("已婚") || s.matches("未婚")) {
            return true;
        }
        return false;
    }

    private boolean isGender(String s) {
        if (s.matches("男") || s.matches("女")) {
            return true;
        }
        return false;
    }

    private boolean isDate(String date) {
        if (date.length() < 10) {
            return false;
        }
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        if (isNumber(year) && isNumber(month) && isNumber(day) && date.charAt(4) == '-' && date.charAt(7) == '-') {
            if (year.substring(0, 2).matches("19") || year.substring(0, 2).matches("20")) {
                if ((month.charAt(0) == '0' && month.charAt(1) != '0') ||
                        (month.charAt(0) == '1' && month.charAt(1) < '3')) {
                    if (day.charAt(0) < '3' || (day.charAt(0) == '3' && day.charAt(1) < '2')) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isNumber(String s) {
        for (int i = 0; i < s.length(); i++) {
            Pattern p = Pattern.compile("[0-9]*");
            Matcher m = p.matcher(String.valueOf(s.charAt(i)));
            if (!m.matches()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.btn_next)
    public void onNextClick() {
        if (isChinese(firstName.getText().toString())
                && isChinese(lastName.getText().toString())) {
            mErrorMsgText.setVisibility(View.GONE);
        } else {
            mErrorMsgText.setVisibility(View.VISIBLE);
            mErrorMsgText.setText(R.string.name_error);
            return;
        }
        if (mBirthDateEditText.getText() != null && isDate(mBirthDateEditText.getText().toString())) {
            mErrorMsgText.setVisibility(View.GONE);
        } else {
            mErrorMsgText.setVisibility(View.VISIBLE);
            mErrorMsgText.setText(R.string.date_error);
            return;
        }
        if (mGenderEditText.getText() != null && isGender(mGenderEditText.getText().toString())) {
            mErrorMsgText.setVisibility(View.GONE);
        } else {
            mErrorMsgText.setVisibility(View.VISIBLE);
            mErrorMsgText.setText(R.string.gender_error);
            return;
        }
        if (mMaritalEditText.getText() != null && isMarital(mMaritalEditText.getText().toString())) {
            mErrorMsgText.setVisibility(View.GONE);
        } else {
            mErrorMsgText.setVisibility(View.VISIBLE);
            mErrorMsgText.setText(R.string.marital_error);
            return;
        }

        if (idNumber.getText().toString().isEmpty() || idNumber.getText().toString().length() < 18) {
            mErrorMsgText.setVisibility(View.VISIBLE);
            mErrorMsgText.setText(R.string.id_number_hint);
            return;
        } else {
            mErrorMsgText.setVisibility(View.GONE);
        }
        DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
        formDTO.firstName = firstName.getText().toString();
        formDTO.lastName = lastName.getText().toString();
        formDTO.firstNameInEng = firstNameEnglish.getText().toString();
        formDTO.lastNameInEng = lastNameEnglish.getText().toString();
        formDTO.dob = mBirthDateEditText.getText().toString();
        formDTO.gender = mGenderEditText.getText().toString();
        formDTO.martialStatus = mMaritalEditText.getText().toString();
        formDTO.idNO = idNumber.getText().toString();
        formDTO.address = address.getText().toString();

        pushFragment(DriveWealthSignupStep5Fragment.class, new Bundle());
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

    @OnTextChanged({R.id.lastName, R.id.firstName, R.id.lastNameEnglish, R.id.firstNameEnglish, R.id.idNumber, R.id.address})
    public void onEditTextChanged(CharSequence text) {
        checkNEnableNextButton();
    }

    @OnClick({R.id.idcard_front, R.id.idcard_back})
    public void onIdcardClick(View view) {
        mLoadImageforId = view.getId();
        showChooseImageDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_GALLERY && data != null) {
            startPhotoZoom(data.getData());
        } else if (requestCode == REQUEST_CAMERA && mFile != null) {
            startPhotoZoom(Uri.fromFile(mFile));
        } else if (requestCode == REQUEST_PHOTO_ZOOM) {

            Bundle bundle = data.getExtras();
            if (bundle != null) {
                DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();

                Bitmap photo = (Bitmap) bundle.get("data");
                ContextWrapper cw = new ContextWrapper(getActivity());
                // path to /data/data/yourapp/app_data/imageDir
                File directory = cw.getDir("dw", Context.MODE_PRIVATE);
                // Create imageDir
                File mypath = null;

                ImageView image = idcardFront;
                if (mLoadImageforId == R.id.idcard_front) {
                    image = idcardFront;
                    mypath = new File(directory, "idcard_front.jpg");
                    formDTO.idcardFront = Uri.fromFile(mypath);

                } else if (mLoadImageforId == R.id.idcard_back) {
                    image = idcardBack;
                    mypath = new File(directory, "idcard_back.jpg");
                    formDTO.idcardBack = Uri.fromFile(mypath);
                }

                image.setImageBitmap(photo);

                FileOutputStream fos = null;
                try {

                    fos = new FileOutputStream(mypath);

                    // Use the compress method on the BitMap object to write image to the OutputStream
                    photo.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                checkNEnableNextButton();
            }
        }
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

    private void askImageFromLibrary() {
        Intent libraryIntent = new Intent(Intent.ACTION_PICK);
        libraryIntent.setType("image/*");
        startActivityForResult(libraryIntent, REQUEST_GALLERY);
    }

    private void askImageFromCamera() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            THToast.show(R.string.photo_no_sdcard);
            return;
        }
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        mFile = new File(Environment.getExternalStorageDirectory(),
                "th_temp.jpg");
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    private void startPhotoZoom(Uri data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 8);
        intent.putExtra("aspectY", 5);
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_PHOTO_ZOOM);
    }

    private void checkNEnableNextButton() {
        DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
        if (lastName.getText().length() > 0 && firstName.getText().length() > 0 &&
                lastNameEnglish.getText().length() > 0 && firstNameEnglish.getText().length() > 0 &&
                idNumber.getText().length() > 0 && address.getText().length() > 0 &&
                formDTO.idcardFront != null && formDTO.idcardBack != null) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }
}
