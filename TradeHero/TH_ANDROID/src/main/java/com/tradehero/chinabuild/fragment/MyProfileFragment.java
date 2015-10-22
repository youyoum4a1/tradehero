package com.tradehero.chinabuild.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.BitmapTypedOutput;
import com.tradehero.th.models.user.auth.CredentialsDTO;
import com.tradehero.th.models.user.auth.EmailCredentialsDTO;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import dagger.Lazy;
import java.io.File;
import javax.inject.Inject;

public class MyProfileFragment extends DashboardFragment implements View.OnClickListener {

    //Photo Request Code
    private static final int REQUEST_GALLERY = 299;
    private static final int REQUEST_CAMERA = 399;
    private static final int REQUEST_PHOTO_ZOOM = 199;

    private MiddleCallback<UserProfileDTO> middleCallbackUpdateUserProfile;
    @InjectView(R.id.photo_layout) RelativeLayout mPhotoLayout;
    @InjectView(R.id.photo) ImageView mPhoto;
    @InjectView(R.id.name_layout) RelativeLayout mNameLayout;
    @InjectView(R.id.name) TextView mName;
    @InjectView(R.id.sign_layout) RelativeLayout mSignLayout;
    @InjectView(R.id.sign) TextView mSign;
    @InjectView(R.id.account_layout) RelativeLayout mAccountLayout;
    @InjectView(R.id.social_layout) RelativeLayout mSocialLayout;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject MainCredentialsPreference mainCredentialsPreference;

    private UserProfileDTO userProfileDTO;
    private String dialogContent;
    private Bitmap photo;

    @Inject Analytics analytics;

    //Photo
    private File file;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(getString(R.string.settings_my_profile));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_my_profile_fragment_layout, container, false);
        ButterKnife.inject(this, view);
        mPhotoLayout.setOnClickListener(this);
        userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());

        ImageLoader.getInstance().displayImage(userProfileDTO.picture, mPhoto, UniversalImageLoader.getAvatarImageLoaderOptions());

        mNameLayout.setOnClickListener(this);
        mSignLayout.setOnClickListener(this);
        mName.setText(userProfileDTO.displayName);
        if (userProfileDTO.signature != null && !userProfileDTO.signature.isEmpty()) {
            mSign.setText(userProfileDTO.signature);
        }
        mAccountLayout.setOnClickListener(this);
        mSocialLayout.setOnClickListener(this);
        CredentialsDTO credentials = mainCredentialsPreference.getCredentials();
        if (credentials.getAuthType().contentEquals(EmailCredentialsDTO.EMAIL_AUTH_TYPE)) {
            mAccountLayout.setVisibility(View.VISIBLE);
        } else {
            mAccountLayout.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_GALLERY && data != null) {
            if(data.getData()!=null){
                startPhotoZoom(data.getData(), 150);
            }
            return;
        }
        if(requestCode==REQUEST_CAMERA){
            startPhotoZoom(Uri.fromFile(file), 150);
            return;
         }
        if(requestCode == REQUEST_PHOTO_ZOOM && data != null){
            storeAndDisplayPhoto(data);
        }
    }


    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        detachMiddleCallbackUpdateUserProfile();
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.photo_layout:
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.ME_PERSONAL_INFORMATION_AVATAR));
                showChooseImageDialog();
                break;
            case R.id.name_layout:
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.ME_PERSONAL_INFORMATION_NAME));
                gotoEditName();
                break;
            case R.id.sign_layout:
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.ME_PERSONAL_INFORMATION_SIGN));
                gotoEditSign();
                break;
            case R.id.account_layout:
                pushFragment(MyEditAccountFragment.class, new Bundle());
                break;
            case R.id.social_layout:
                pushFragment(MySocialFragment.class, new Bundle());
                break;
        }
    }

    private void gotoEditName() {
        if (userProfileDTO == null) {
            return;
        }
        if (userProfileDTO.isVisitor) {
            dialogContent = getString(R.string.dialog_profile_suggest_signin);
            showSuggestLoginDialogFragment(dialogContent);
        } else {
            pushFragment(MyEditNameFragment.class, new Bundle());
        }
    }

    private void gotoEditSign() {
        if (userProfileDTO == null) {
            return;
        }
        if (userProfileDTO.isVisitor) {
            dialogContent = getString(R.string.dialog_profile_suggest_signin);
            showSuggestLoginDialogFragment(dialogContent);
        } else {
            pushFragment(MyEditSignFragment.class, new Bundle());
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

    private void updatePhoto() {
        UserFormDTO userFormDTO = createForm();
        detachMiddleCallbackUpdateUserProfile();
        middleCallbackUpdateUserProfile = userServiceWrapper.get().updatePhoto(
                currentUserId.toUserBaseKey(),
                userFormDTO,
                createUpdateUserProfileCallback());
    }

    private void detachMiddleCallbackUpdateUserProfile() {
        if (middleCallbackUpdateUserProfile != null) {
            middleCallbackUpdateUserProfile.setPrimaryCallback(null);
        }
        middleCallbackUpdateUserProfile = null;
    }

    public UserFormDTO createForm() {
        UserFormDTO created = new UserFormDTO();
        created.profilePicture = safeCreateProfilePhoto();
        return created;
    }

    protected BitmapTypedOutput safeCreateProfilePhoto() {
        BitmapTypedOutput created = null;
        if(photo==null){
            return null;
        }
        created = new BitmapTypedOutput(BitmapTypedOutput.TYPE_JPEG, photo, String.valueOf(System.currentTimeMillis()), 75);
        return created;
    }

    private THCallback<UserProfileDTO> createUpdateUserProfileCallback() {
        return new THCallback<UserProfileDTO>() {
            @Override
            protected void success(UserProfileDTO userProfileDTO, THResponse thResponse) {
                userProfileCache.put(currentUserId.toUserBaseKey(), userProfileDTO);
                THToast.show(R.string.settings_update_profile_successful);
            }

            @Override
            protected void failure(THException ex) {
                THToast.show(ex.getMessage());
            }
        };
    }

    private void startPhotoZoom(Uri data, int size){
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

    private void storeAndDisplayPhoto(Intent data){
        Bundle bundle = data.getExtras();
        if(bundle != null){
            photo = (Bitmap) bundle.get("data");
            if(photo!=null){
                mPhoto.setImageBitmap(photo);
                updatePhoto();
            }
        }
    }


}
