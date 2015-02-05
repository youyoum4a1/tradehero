package com.tradehero.th.fragments.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.activities.ActivityResultRequester;
import com.tradehero.common.utils.FileUtils;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.graphics.BitmapTypedOutput;
import com.tradehero.th.models.graphics.BitmapTypedOutputFactory;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.GraphicUtil;
import com.tradehero.th.widget.MatchingPasswordText;
import com.tradehero.th.widget.ServerValidatedEmailText;
import com.tradehero.th.widget.ServerValidatedUsernameText;
import com.tradehero.th.widget.ValidatedPasswordText;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Actions;
import rx.functions.Func8;
import timber.log.Timber;

import static com.tradehero.th.utils.Constants.Auth.PARAM_ACCOUNT_TYPE;

public class ProfileInfoView extends LinearLayout
    implements ActivityResultRequester
{
    @InjectView(R.id.authentication_sign_up_email) ServerValidatedEmailText email;
    @InjectView(R.id.authentication_sign_up_password) ValidatedPasswordText password;
    @InjectView(R.id.authentication_sign_up_confirm_password) MatchingPasswordText confirmPassword;
    @InjectView(R.id.authentication_sign_up_username) ServerValidatedUsernameText displayName;
    @InjectView(R.id.authentication_sign_up_referral_code) EditText referralCode;
    @InjectView(R.id.et_firstname) EditText firstName;
    @InjectView(R.id.et_lastname) EditText lastName;
    @InjectView(R.id.image_optional) @Optional ImageView profileImage;

    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;
    @Inject Provider<UserFormDTO.Builder2> userFormBuilderProvider;
    @Inject AccountManager accountManager;

    ProgressDialog progressDialog;
    private UserProfileDTO userProfileDTO;
    private String newImagePath;
    private AlertDialog imagePickerDialog;

    public ProfileInfoView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        displayProfileImage();
        if (!isInEditMode())
        {
            populateCredentials();
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public boolean areFieldsValid()
    {
        return (email == null || email.getVisibility() == GONE || email.isValid()) &&
                (password == null || password.getVisibility() == GONE || password.isValid()) &&
                (confirmPassword == null || confirmPassword.getVisibility() == GONE || confirmPassword.isValid()) &&
                (displayName == null || displayName.isValid());
    }

    public void handleDataFromLibrary(Intent data)
    {
        Uri selectedImageUri = data.getData();
        if (selectedImageUri != null)
        {
            String selectedPath = FileUtils.getPath(getContext(), selectedImageUri);
            setNewImagePath(selectedPath);
        }
        else
        {
            AlertDialogRxUtil.buildDefault(getContext())
                    .setTitle(R.string.error_fetch_image_library)
                    .setMessage(R.string.error_fetch_image_library)
                    .setPositiveButton(R.string.cancel)
                    .build()
                    .subscribe(Actions.empty(), Actions.empty());
        }
    }

    public void setNewImagePath(String newImagePath)
    {
        this.newImagePath = newImagePath;
        displayProfileImage();
    }

    protected BitmapTypedOutput safeCreateProfilePhoto()
    {
        BitmapTypedOutput created = null;
        if (newImagePath != null)
        {
            try
            {
                created = BitmapTypedOutputFactory.createForProfilePhoto(
                        getResources(), newImagePath);
            }
            catch (OutOfMemoryError e)
            {
                THToast.show(R.string.error_decode_image_memory);
            }
        }
        return created;
    }

    public void populate(UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
        firstName.setText(userProfileDTO.firstName);
        lastName.setText(userProfileDTO.lastName);
        displayName.setText(userProfileDTO.displayName);
        displayName.setOriginalUsernameValue(userProfileDTO.displayName);
        referralCode.setText(userProfileDTO.inviteCode);
        if (userProfileDTO.inviteCode != null)
        {
            referralCode.setEnabled(false);
        }
        String currentEmail = email.getText().toString();
        if (currentEmail == null || currentEmail.isEmpty())
        {
            email.setText(userProfileDTO.email);
        }
        displayProfileImage();
    }

    //region Display user information
    public void displayProfileImage()
    {
        if (newImagePath != null)
        {
            Bitmap decoded = GraphicUtil.decodeBitmapForProfile(getResources(), newImagePath);
            if (decoded != null)
            {
                displayProfileImage(decoded);
            }
            else
            {
                displayDefaultProfileImage();
            }
        }
        else if (userProfileDTO != null)
        {
            displayProfileImage(userProfileDTO);
        }
        else
        {
            displayDefaultProfileImage();
        }
    }

    public void displayProfileImage(Bitmap newImage)
    {
        if (this.profileImage != null)
        {
            profileImage.setImageBitmap(userPhotoTransformation.transform(newImage));
        }
    }

    public void displayProfileImage(UserBaseDTO userBaseDTO)
    {
        if (this.profileImage != null)
        {
            if (userBaseDTO.picture == null)
            {
                displayDefaultProfileImage();
            }
            else
            {
                picasso.load(userBaseDTO.picture)
                        .transform(userPhotoTransformation)
                        .into(profileImage, new Callback()
                        {
                            @Override public void onSuccess()
                            {
                            }

                            @Override public void onError()
                            {
                                displayDefaultProfileImage();
                            }
                        });
            }
        }
    }

    public void displayDefaultProfileImage()
    {
        if (this.profileImage != null && picasso != null)
        {
            picasso.load(R.drawable.superman_facebook)
                    .transform(userPhotoTransformation)
                    .into(profileImage);
        }
    }
    //endregion

    private void populateCredentials()
    {
        Account[] accounts = accountManager.getAccountsByType(PARAM_ACCOUNT_TYPE);
        if (accounts != null && accounts.length > 0)
        {
            String emailValue = null, passwordValue = null;
            for (Account account: accounts)
            {
                if (account.name != null)
                {
                    String currentPassword = accountManager.getPassword(account);
                    if (currentPassword != null)
                    {
                        emailValue = account.name;
                        passwordValue = currentPassword;

                        // TODO what if we have more than 1 account with both email & pass
                        break;
                    }

                    emailValue = account.name;
                }
            }

            this.email.setText(emailValue);
            this.password.setText(passwordValue);
            this.confirmPassword.setText(passwordValue);

            this.password.setValidateOnlyIfNotEmpty(passwordValue == null);
            this.confirmPassword.setValidateOnlyIfNotEmpty(passwordValue == null);
        }
    }

    @OnClick(R.id.image_optional) @Optional
    protected void showImageFromDialog()
    {
        ImagePickerView imagePickerView = (ImagePickerView) LayoutInflater.from(getContext())
                .inflate(R.layout.image_picker, null);
        imagePickerDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.user_profile_choose_image_from_choice)
                .setNegativeButton(R.string.cancel, null)
                .setView(imagePickerView)
                .create();
        imagePickerDialog.show();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (imagePickerDialog != null)
        {
            imagePickerDialog.dismiss();
        }
        // handle image upload
        if (resultCode == Activity.RESULT_OK)
        {
            if ((requestCode == ImagePickerView.REQUEST_CAMERA || requestCode == ImagePickerView.REQUEST_GALLERY) && data != null)
            {
                try
                {
                    handleDataFromLibrary(data);
                }
                catch (OutOfMemoryError e)
                {
                    THToast.show(R.string.error_decode_image_memory);
                }
                catch (Exception e)
                {
                    THToast.show(R.string.error_fetch_image_library);
                    Timber.e(e, "Failed to extract image from library");
                }
            }
            else if (requestCode == ImagePickerView.REQUEST_GALLERY)
            {
                Timber.e(new Exception("Got null data from library"), "");
            }
        }
        else if (resultCode != Activity.RESULT_CANCELED)
        {
            Timber.e(new Exception("Failed to get image from library, resultCode: " + resultCode), "");
        }
    }

    public Observable<UserFormDTO> obtainUserFormDTO()
    {
        return Observable.combineLatest(
                WidgetObservable.text(email, true),
                WidgetObservable.text(password, true),
                WidgetObservable.text(confirmPassword, true),
                WidgetObservable.text(displayName, true),
                WidgetObservable.text(referralCode, true),
                WidgetObservable.text(firstName, true),
                WidgetObservable.text(lastName, true),
                Observable.just(profileImage),
                new Func8<OnTextChangeEvent, OnTextChangeEvent, OnTextChangeEvent, OnTextChangeEvent, OnTextChangeEvent, OnTextChangeEvent, OnTextChangeEvent, ImageView, UserFormDTO>()
                {
                    @Override public UserFormDTO call(
                            OnTextChangeEvent serverValidatedEmailText,
                            OnTextChangeEvent validatedPasswordText,
                            OnTextChangeEvent matchingPasswordText,
                            OnTextChangeEvent serverValidatedUsernameText,
                            OnTextChangeEvent referralCode1,
                            OnTextChangeEvent firstName1,
                            OnTextChangeEvent lastName1,
                            ImageView profileImage)
                    {
                        email.forceValidate();
                        password.forceValidate();
                        confirmPassword.forceValidate();
                        displayName.forceValidate();
                        return userFormBuilderProvider.get()
                                .email(email.getText().toString())
                                .password(password.getText().toString())
                                .displayName(displayName.getText().toString())
                                .inviteCode(referralCode.getText().toString())
                                .firstName(firstName.getText().toString())
                                .lastName(lastName.getText().toString())
                                .profilePicture(safeCreateProfilePhoto())
                                .build();
                    }
                }

        );
    }
}
