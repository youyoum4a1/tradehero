package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
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
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.fragments.settings.photo.ChooseImageFromAdapter;
import com.tradehero.th.fragments.settings.photo.ChooseImageFromCameraDTO;
import com.tradehero.th.fragments.settings.photo.ChooseImageFromDTO;
import com.tradehero.th.fragments.settings.photo.ChooseImageFromDTOFactory;
import com.tradehero.th.fragments.settings.photo.ChooseImageFromLibraryDTO;
import com.tradehero.th.models.graphics.BitmapTypedOutput;
import com.tradehero.th.models.graphics.BitmapTypedOutputFactory;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.user.auth.EmailCredentialsDTO;
import com.tradehero.th.persistence.prefs.AuthHeader;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.BitmapForProfileFactory;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.MatchingPasswordText;
import com.tradehero.th.widget.ServerValidatedEmailText;
import com.tradehero.th.widget.ServerValidatedUsernameText;
import com.tradehero.th.widget.ValidatedPasswordText;
import com.tradehero.th.widget.ValidationListener;
import dagger.Lazy;
import java.util.Map;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class ProfileInfoView extends LinearLayout
{
    @InjectView(R.id.authentication_sign_up_email) ServerValidatedEmailText email;
    @InjectView(R.id.authentication_sign_up_password) ValidatedPasswordText password;
    @InjectView(R.id.authentication_sign_up_confirm_password) MatchingPasswordText confirmPassword;
    @InjectView(R.id.authentication_sign_up_username) ServerValidatedUsernameText displayName;
    @InjectView(R.id.et_firstname) EditText firstName;
    @InjectView(R.id.et_lastname) EditText lastName;
    @InjectView(R.id.image_optional) @Optional ImageView profileImage;

    @Inject ChooseImageFromDTOFactory chooseImageFromDTOFactory;
    @Inject AlertDialogUtil alertDialogUtil;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;
    @Inject BitmapForProfileFactory bitmapForProfileFactory;
    @Inject BitmapTypedOutputFactory bitmapTypedOutputFactory;
    @Inject @AuthHeader Lazy<String> authenticationHeader;

    ProgressDialog progressDialog;
    private UserBaseDTO userBaseDTO;
    private String newImagePath;
    private Listener listener;

    //<editor-fold desc="Constructors">
    public ProfileInfoView(Context context)
    {
        super(context);
    }

    public ProfileInfoView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ProfileInfoView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
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

    public void setListener(Listener listener)
    {
        this.listener = listener;
    }

    public void forceValidateFields()
    {
        if (email != null && email.getVisibility() == VISIBLE)
        {
            email.forceValidate();
        }
        if (password != null && password.getVisibility() == VISIBLE)
        {
            password.forceValidate();
        }
        if (confirmPassword != null && confirmPassword.getVisibility() == VISIBLE)
        {
            confirmPassword.forceValidate();
        }
        if (displayName != null)
        {
            displayName.forceValidate();
        }
    }

    public boolean areFieldsValid()
    {
        return (email == null || email.getVisibility() == GONE || email.isValid()) &&
                (password == null || password.getVisibility() == GONE || password.isValid()) &&
                (confirmPassword == null || confirmPassword.getVisibility() == GONE || confirmPassword.isValid()) &&
                (displayName == null || displayName.isValid());
    }

    public void setOnTouchListenerOnFields(View.OnTouchListener touchListener)
    {
        if (email != null)
        {
            email.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..
        }
        if (password != null)
        {
            password.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..
        }
        if (confirmPassword != null)
        {
            confirmPassword.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..
        }
        if (displayName != null)
        {
            displayName.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..
        }
        if (firstName != null)
        {
            firstName.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..
        }
        if (lastName != null)
        {
            lastName.setOnTouchListener(touchListener); // HACK: force this to focus instead of the TabHost stealing focus..
        }
    }

    public void addValidationListenerOnFields(ValidationListener listener)
    {
        if (email != null)
        {
            email.setListener(listener);
        }
        if (password != null)
        {
            password.setListener(listener);
        }
        if (confirmPassword != null)
        {
            confirmPassword.setListener(listener);
        }
        if (displayName != null)
        {
            displayName.setListener(listener);
        }
    }

    public void removeAllListenersOnFields()
    {
        if (email != null)
        {
            email.setListener(null);
        }
        if (password != null)
        {
            password.setListener(null);
        }
        if (confirmPassword != null)
        {
            confirmPassword.setListener(null);
        }
        if (displayName != null)
        {
            displayName.setListener(null);
        }
    }

    public void setNullOnFields()
    {
        email = null;
        password = null;
        confirmPassword = null;
        displayName = null;
        firstName = null;
        lastName = null;
        progressDialog = null;
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
        populateUserFormMapFromEditable(map, UserFormFactory.KEY_EMAIL, email.getText());
        populateUserFormMapFromEditable(map, UserFormFactory.KEY_PASSWORD, password.getText());
        populateUserFormMapFromEditable(map, UserFormFactory.KEY_PASSWORD_CONFIRM, confirmPassword.getText());
        populateUserFormMapFromEditable(map, UserFormFactory.KEY_DISPLAY_NAME, displayName.getText());
        populateUserFormMapFromEditable(map, UserFormFactory.KEY_FIRST_NAME, firstName.getText());
        populateUserFormMapFromEditable(map, UserFormFactory.KEY_LAST_NAME, lastName.getText());
        if (newImagePath != null)
        {
            map.put(UserFormFactory.KEY_PROFILE_PICTURE, safeCreateProfilePhoto());
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
            }
            catch (OutOfMemoryError e)
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

    public void populate(UserBaseDTO userBaseDTO)
    {
        this.userBaseDTO = userBaseDTO;
        this.firstName.setText(userBaseDTO.firstName);
        this.lastName.setText(userBaseDTO.lastName);
        this.displayName.setText(userBaseDTO.displayName);
        this.displayName.setOriginalUsernameValue(userBaseDTO.displayName);
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
        else if (userBaseDTO != null)
        {
            displayProfileImage(userBaseDTO);
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
            Timber.e(new NullPointerException("credentials were null current auth type " +  authenticationHeader.get()), "");
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
                }
                if (credentials.has("password"))
                {
                    passwordValue = credentials.getString("password");
                }
            }
            catch (JSONException e)
            {
                Timber.e(e, "populateCredentials");
            }
            this.email.setText(emailValue);
            this.password.setText(passwordValue);
            this.confirmPassword.setText(passwordValue);

            if (emailValue == null)
            {
                this.email.setVisibility(GONE);
            }
            if (passwordValue == null)
            {
                this.password.setVisibility(GONE);
                this.confirmPassword.setVisibility(GONE);
            }
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
        ChooseImageFromAdapter adapter = new ChooseImageFromAdapter(
                getContext(),
                R.layout.choose_from_item);
        adapter.addAll(chooseImageFromDTOFactory.getAll(getContext()));
        alertDialogUtil.popWithNegativeButton(getContext(),
                getContext().getString(R.string.user_profile_choose_image_from_choice),
                null, getContext().getString(R.string.cancel),
                adapter, createChooseImageDialogClickListener(),
                null);
    }

    protected void handleChooseImage(ChooseImageFromDTO chooseImageFrom)
    {
        if (chooseImageFrom instanceof ChooseImageFromCameraDTO)
        {
            notifyImageFromCameraRequested();
        }
        else if (chooseImageFrom instanceof ChooseImageFromLibraryDTO)
        {
            notifyImageFromLibraryRequested();
        }
        else
        {
            Timber.e(new Exception("unhandled ChooseFrom type " + chooseImageFrom), "");
        }
    }

    protected void notifyImageFromCameraRequested()
    {
        Listener listenerCopy = listener;
        if (listenerCopy != null)
        {
            listenerCopy.onImageFromCameraRequested();
        }
    }

    protected void notifyImageFromLibraryRequested()
    {
        Listener listenerCopy = listener;
        if (listenerCopy != null)
        {
            listenerCopy.onImageFromLibraryRequested();
        }
    }

    protected AlertDialogUtil.OnClickListener<ChooseImageFromDTO> createChooseImageDialogClickListener()
    {
        return new ProfileInfoViewChooseImageDialogClickListener();
    }

    protected class ProfileInfoViewChooseImageDialogClickListener implements AlertDialogUtil.OnClickListener<ChooseImageFromDTO>
    {
        @Override public void onClick(ChooseImageFromDTO which)
        {
            handleChooseImage(which);
        }
    }

    public static interface Listener
    {
        void onUpdateRequested();
        void onImageFromCameraRequested();
        void onImageFromLibraryRequested();
    }
}
