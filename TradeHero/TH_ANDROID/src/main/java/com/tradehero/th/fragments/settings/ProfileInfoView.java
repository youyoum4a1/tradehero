package com.tradehero.th.fragments.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
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
import com.tradehero.th.models.user.auth.EmailCredentialsDTO;
import com.tradehero.th.persistence.prefs.AuthHeader;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.BitmapForProfileFactory;
import com.tradehero.th.widget.MatchingPasswordText;
import com.tradehero.th.widget.ServerValidatedEmailText;
import com.tradehero.th.widget.ServerValidatedUsernameText;
import com.tradehero.th.widget.ValidatedPasswordText;
import dagger.Lazy;
import java.util.Map;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import timber.log.Timber;

public class ProfileInfoView extends LinearLayout
{
    @InjectView(R.id.authentication_sign_up_email) ServerValidatedEmailText email;
    @InjectView(R.id.authentication_sign_up_password) ValidatedPasswordText password;
    @InjectView(R.id.authentication_sign_up_confirm_password) MatchingPasswordText confirmPassword;
    @InjectView(R.id.authentication_sign_up_username) ServerValidatedUsernameText displayName;
    @InjectView(R.id.authentication_sign_up_referral_code) EditText referralCode;
    @InjectView(R.id.et_firstname) EditText firstName;
    @InjectView(R.id.et_lastname) EditText lastName;
    @InjectView(R.id.image_optional) @Optional ImageView profileImage;

    @Inject AlertDialogUtil alertDialogUtil;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;
    @Inject BitmapForProfileFactory bitmapForProfileFactory;
    @Inject BitmapTypedOutputFactory bitmapTypedOutputFactory;
    @Inject @AuthHeader Lazy<String> authenticationHeader;
    @Inject Activity activity;

    ProgressDialog progressDialog;
    private UserProfileDTO userProfileDTO;
    private String newImagePath;

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
            alertDialogUtil.popWithNegativeButton(getContext(),
                    R.string.error_fetch_image_library,
                    R.string.error_fetch_image_library,
                    R.string.cancel);
        }
    }

    public void setNewImagePath(String newImagePath)
    {
        this.newImagePath = newImagePath;
        displayProfileImage();
    }

    public UserFormDTO createForm()
    {
        UserFormDTO created = new UserFormDTO();
        created.email = getTextValue(email);
        created.password = getTextValue(password);
        created.passwordConfirmation = getTextValue(confirmPassword);
        created.displayName = getTextValue(displayName);
        created.firstName = getTextValue(firstName);
        created.lastName = getTextValue(lastName);
        created.profilePicture = safeCreateProfilePhoto();
        return created;
    }

    public void populateUserFormMap(Map<String, Object> map)
    {
        populateUserFormMapFromEditable(map, UserFormDTO.KEY_EMAIL, email.getText());
        populateUserFormMapFromEditable(map, UserFormDTO.KEY_PASSWORD, password.getText());
        populateUserFormMapFromEditable(map, UserFormDTO.KEY_PASSWORD_CONFIRM, confirmPassword.getText());
        populateUserFormMapFromEditable(map, UserFormDTO.KEY_DISPLAY_NAME, displayName.getText());
        populateUserFormMapFromEditable(map, UserFormDTO.KEY_INVITE_CODE, referralCode.getText());
        populateUserFormMapFromEditable(map, UserFormDTO.KEY_FIRST_NAME, firstName.getText());
        populateUserFormMapFromEditable(map, UserFormDTO.KEY_LAST_NAME, lastName.getText());
        if (newImagePath != null)
        {
            map.put(UserFormDTO.KEY_PROFILE_PICTURE, safeCreateProfilePhoto());
        }
    }

    protected BitmapTypedOutput safeCreateProfilePhoto()
    {
        BitmapTypedOutput created = null;
        if (newImagePath != null)
        {
            try
            {
                created = bitmapTypedOutputFactory.createForProfilePhoto(
                        getResources(), bitmapForProfileFactory, newImagePath);
            } catch (OutOfMemoryError e)
            {
                THToast.show(R.string.error_decode_image_memory);
            }
        }
        return created;
    }

    private void populateUserFormMapFromEditable(Map<String, Object> toFill, String key, Editable toPick)
    {
        if (toPick != null)
        {
            toFill.put(key, toPick.toString());
        }
    }

    private String getTextValue(TextView textView)
    {
        if (textView != null)
        {
            return textView.getText().toString();
        }
        else
        {
            return null;
        }
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

    public void displayProfileImage()
    {
        if (newImagePath != null)
        {
            Bitmap decoded = bitmapForProfileFactory.decodeBitmapForProfile(getResources(), newImagePath);
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

    // TODO pass something else
    public void populateCredentials(JSONObject credentials)
    {
        if (credentials == null)
        {
            Timber.e(new NullPointerException("credentials were null current auth type " + authenticationHeader.get()), "");
            THToast.show(R.string.error_fetch_your_user_profile);
        }
        else
        {
            String emailValue = null, passwordValue = null;
            try
            {
                // We test here just to reduce the number of errors sent to Crashlytics
                if (credentials.has("email"))
                {
                    emailValue = credentials.getString("email");
                    this.email.setText(emailValue);
                }
                if (credentials.has("password"))
                {
                    passwordValue = credentials.getString("password");
                }
            } catch (JSONException e)
            {
                Timber.e(e, "populateCredentials");
            }
            this.password.setText(passwordValue);
            this.confirmPassword.setText(passwordValue);

            this.password.setValidateOnlyIfNotEmpty(passwordValue == null);
            this.confirmPassword.setValidateOnlyIfNotEmpty(passwordValue == null);
        }
    }

    public EmailCredentialsDTO getEmailCredentialsDTO()
    {
        return new EmailCredentialsDTO(
                email.getText().toString(),
                password.getText().toString());
    }

    @OnClick(R.id.image_optional) @Optional
    protected void showImageFromDialog()
    {
        ImagePickerView imagePickerView = (ImagePickerView) LayoutInflater.from(getContext())
                .inflate(R.layout.image_picker, null);
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.user_profile_choose_image_from_choice)
                .setNegativeButton(R.string.cancel, null)
                .setView(imagePickerView)
                .create();
        alertDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
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
                } catch (Exception e)
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
            Timber.e(new Exception("Failed to get image from libray, resultCode: " + resultCode), "");
        }
    }

    public Observable<UserFormDTO> obtainUserFormDTO()
    {
        return null;
    }
}
