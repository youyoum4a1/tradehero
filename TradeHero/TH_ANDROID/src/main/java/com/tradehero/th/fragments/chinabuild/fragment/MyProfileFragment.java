package com.tradehero.th.fragments.chinabuild.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.squareup.picasso.Picasso;
import com.tradehero.common.utils.FileUtils;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.BitmapTypedOutput;
import com.tradehero.th.models.graphics.BitmapTypedOutputFactory;
import com.tradehero.th.models.user.auth.CredentialsDTO;
import com.tradehero.th.models.user.auth.EmailCredentialsDTO;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.BitmapForProfileFactory;
import dagger.Lazy;

import java.util.Date;
import java.util.Random;
import javax.inject.Inject;

import timber.log.Timber;

public class MyProfileFragment extends DashboardFragment implements View.OnClickListener {
    private static final int REQUEST_GALLERY = new Random(new Date().getTime()).nextInt(Short.MAX_VALUE);
    private static final int REQUEST_CAMERA = new Random(new Date().getTime() + 1).nextInt(Short.MAX_VALUE);
    private String newImagePath;
    private MiddleCallback<UserProfileDTO> middleCallbackUpdateUserProfile;
    @InjectView(R.id.photo_layout) RelativeLayout mPhotoLayout;
    @InjectView(R.id.photo) ImageView mPhoto;
    @InjectView(R.id.name_layout) RelativeLayout mNameLayout;
    @InjectView(R.id.name) TextView mName;
    @InjectView(R.id.account_layout) RelativeLayout mAccountLayout;
    @InjectView(R.id.social_layout) RelativeLayout mSocialLayout;
    @Inject BitmapForProfileFactory bitmapForProfileFactory;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject Picasso picasso;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject BitmapTypedOutputFactory bitmapTypedOutputFactory;
    @Inject MainCredentialsPreference mainCredentialsPreference;

    private UserProfileDTO userProfileDTO;
    private String dialogContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.settings_my_profile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_my_profile_fragment_layout, container, false);
        ButterKnife.inject(this, view);
        mPhotoLayout.setOnClickListener(this);
        userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        picasso.load(userProfileDTO.picture)
                .placeholder(R.drawable.superman_facebook)
                .into(mPhoto);
        mNameLayout.setOnClickListener(this);
        mName.setText(userProfileDTO.displayName);
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if ((requestCode == REQUEST_CAMERA || requestCode == REQUEST_GALLERY) && data != null) {
                try {
                    handleDataFromLibrary(data);
                    updatePhoto();
                } catch (OutOfMemoryError e) {
                    THToast.show(R.string.error_decode_image_memory);
                } catch (Exception e) {
                    THToast.show(R.string.error_fetch_image_library);
                    Timber.e(e, "Failed to extract image from library");
                }
            } else if (requestCode == REQUEST_GALLERY) {
                Timber.e(new Exception("Got null data from library"), "");
            }
        } else if (resultCode != Activity.RESULT_CANCELED) {
            Timber.e(new Exception("Failed to get image from libray, resultCode: " + resultCode), "");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        detachMiddleCallbackUpdateUserProfile();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.photo_layout:
                showChooseImageDialog();
                break;
            case R.id.name_layout:
                gotoEditName();
                break;
            case R.id.account_layout:
                goToFragment(MyEditAccountFragment.class);
                break;
            case R.id.social_layout:
                goToFragment(MySocialFragment.class);
                break;
        }
    }

    private void gotoEditName() {
        if (userProfileDTO == null) {
            return;
        }
        if (userProfileDTO.isVisitor) {
            dialogContent = getActivity().getResources().getString(R.string.dialog_profile_suggest_signin);
            showSuggestLoginDialogFragment(dialogContent);
        } else {
            goToFragment(MyEditNameFragment.class);
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
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
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
        if (newImagePath != null) {
            Bitmap decoded = bitmapForProfileFactory.decodeBitmapForProfile(getResources(), newImagePath);
            if (decoded != null) {
                mPhoto.setImageBitmap(decoded);
                return;
            }
        }
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
        if (newImagePath != null) {
            try {
                created = bitmapTypedOutputFactory.createForProfilePhoto(
                        getResources(), bitmapForProfileFactory, newImagePath);
            } catch (OutOfMemoryError e) {
                THToast.show(R.string.error_decode_image_memory);
            }
        }
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
}
