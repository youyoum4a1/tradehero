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
import android.view.Menu;
import android.view.MenuInflater;
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
import com.tradehero.th.fragments.base.DashboardFragment;
import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class DriveWealthSignupStep4Fragment extends DashboardFragment {

    @Inject DriveWealthManager mDriveWealthManager;

    @InjectView(R.id.lastName)
    EditText lastName;
    @InjectView(R.id.firstName)
    EditText firstName;
    @InjectView(R.id.lastNameEnglish)
    EditText lastNameEnglish;
    @InjectView(R.id.firstNameEnglish)
    EditText firstNameEnglish;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        setHeadViewMiddleMain("个人资料(4/7)");
        setHeadViewRight0(getString(R.string.cancel));
    }

    @Override
    public void onClickHeadRight0() {
        getActivity().finish();
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
                    if (isChinese(firstName.getText().toString())) {
                        THToast.show(R.string.name_error);
                    }
                }
            }
        });
        lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (isChinese(lastName.getText().toString())) {
                        THToast.show(R.string.name_error);
                    }
                }
            }
        });
        idNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (idNumber.getText().toString().isEmpty() || idNumber.getText().toString().length() < 18) {
                        THToast.show(R.string.id_number_hint);
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
        if (isChinese(firstName.getText().toString())
                && isChinese(lastName.getText().toString())) {
            mErrorMsgText.setVisibility(View.GONE);
        } else {
            mErrorMsgText.setVisibility(View.VISIBLE);
            mErrorMsgText.setText(R.string.name_error);
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
        formDTO.idNO = idNumber.getText().toString();
        formDTO.address = address.getText().toString();

        pushFragment(DriveWealthSignupStep5Fragment.class, new Bundle());
    }

    private boolean isChinese(String text) {
        for (int i=0;i<text.length();i++) {
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
        } else if(requestCode == REQUEST_CAMERA && mFile != null){
            startPhotoZoom(Uri.fromFile(mFile));
        } else if (requestCode == REQUEST_PHOTO_ZOOM) {

            Bundle bundle = data.getExtras();
            if(bundle != null){
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
                    mypath = new File(directory,"idcard_front.jpg");
                    formDTO.idcardFront = Uri.fromFile(mypath);

                } else if (mLoadImageforId == R.id.idcard_back) {
                    image = idcardBack;
                    mypath = new File(directory,"idcard_back.jpg");
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

    private void startPhotoZoom(Uri data){
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
