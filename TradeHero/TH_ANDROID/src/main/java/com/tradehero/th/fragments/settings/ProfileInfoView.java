package com.tradehero.th.fragments.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.activities.ActivityResultRequester;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.graphics.BitmapTypedOutput;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.widget.validation.DisplayNameValidatedText;
import com.tradehero.th.widget.validation.DisplayNameValidator;
import com.tradehero.th.widget.validation.MatchingPasswordText;
import com.tradehero.th.widget.validation.PasswordConfirmTextValidator;
import com.tradehero.th.widget.validation.PasswordValidatedText;
import com.tradehero.th.widget.validation.TextValidator;
import com.tradehero.th.widget.validation.ValidatedText;
import com.tradehero.th.widget.validation.ValidatedView;
import com.tradehero.th.widget.validation.ValidationMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.Observer;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func4;
import rx.functions.Func8;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

import static com.tradehero.th.utils.Constants.Auth.PARAM_ACCOUNT_TYPE;

public class ProfileInfoView extends LinearLayout
        implements ActivityResultRequester
{
    public static final int REQUEST_GALLERY = 1309;
    public static final int REQUEST_CAMERA = 1310;
    private final static int REQUEST_PHOTO_ZOOM = 1311;

    @InjectView(R.id.authentication_sign_up_email) ValidatedText email;
    TextValidator emailValidator;
    @InjectView(R.id.authentication_sign_up_password) PasswordValidatedText password;
    TextValidator passwordValidator;
    @InjectView(R.id.authentication_sign_up_confirm_password) MatchingPasswordText confirmPassword;
    PasswordConfirmTextValidator confirmPasswordValidator;
    TextWatcher targetPasswordWatcher;
    @InjectView(R.id.authentication_sign_up_username) DisplayNameValidatedText displayName;
    DisplayNameValidator displayNameValidator;
    @InjectView(R.id.authentication_sign_up_referral_code) EditText referralCode;
    @InjectView(R.id.et_firstname) EditText firstName;
    @InjectView(R.id.et_lastname) EditText lastName;
    @InjectView(R.id.image_optional) @Optional ImageView profileImage;

    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;
    @Inject Provider<UserFormDTO.Builder2> userFormBuilderProvider;
    @Inject DashboardNavigator dashboardNavigator;

    @NonNull final AccountManager accountManager;
    private UserProfileDTO userProfileDTO;
    @NonNull protected SubscriptionList subscriptions;
    private File mCurrentPhotoFile;
    private File croppedPhotoFile;
    private int currentRequest = -1;

    //<editor-fold desc="Constructors">
    public ProfileInfoView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
        subscriptions = new SubscriptionList();
        accountManager = AccountManager.get(context);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        emailValidator = email.getValidator();
        passwordValidator = password.getValidator();
        confirmPasswordValidator = confirmPassword.getValidator();
        displayNameValidator = displayName.getValidator();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        displayProfileImage();

        email.setOnFocusChangeListener(emailValidator);
        email.addTextChangedListener(emailValidator);
        subscriptions.add(emailValidator.getValidationMessageObservable().subscribe(createValidatorObserver(email)));
        password.setOnFocusChangeListener(passwordValidator);
        password.addTextChangedListener(passwordValidator);
        subscriptions.add(passwordValidator.getValidationMessageObservable().subscribe(createValidatorObserver(password)));
        confirmPassword.setOnFocusChangeListener(confirmPasswordValidator);
        confirmPassword.addTextChangedListener(confirmPasswordValidator);
        targetPasswordWatcher = confirmPasswordValidator.getPasswordTextWatcher();
        password.addTextChangedListener(targetPasswordWatcher);
        subscriptions.add(confirmPasswordValidator.getValidationMessageObservable().subscribe(createValidatorObserver(confirmPassword)));
        displayName.setOnFocusChangeListener(displayNameValidator);
        displayName.addTextChangedListener(displayNameValidator);

        if (!isInEditMode())
        {
            populateCredentials();
        }

        if (userProfileDTO != null)
        {
            populate(userProfileDTO);
        }
        subscriptions.add(displayNameValidator.getValidationMessageObservable().subscribe(createValidatorObserver(displayName)));
    }

    @Override protected void onDetachedFromWindow()
    {
        displayName.removeTextChangedListener(displayNameValidator);
        displayName.setOnFocusChangeListener(null);
        password.removeTextChangedListener(targetPasswordWatcher);
        targetPasswordWatcher = null;
        confirmPassword.removeTextChangedListener(confirmPasswordValidator);
        confirmPassword.setOnFocusChangeListener(null);
        password.removeTextChangedListener(passwordValidator);
        password.setOnFocusChangeListener(null);
        email.removeTextChangedListener(emailValidator);
        email.setOnFocusChangeListener(null);
        subscriptions.unsubscribe();
        subscriptions = new SubscriptionList();
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @NonNull protected Observer<ValidationMessage> createValidatorObserver(@NonNull final ValidatedText validatedText)
    {
        return new Observer<ValidationMessage>()
        {
            @Nullable private String previousMessage;

            @Override public void onNext(ValidationMessage validationMessage)
            {
                validatedText.setStatus(validationMessage.getValidStatus());
                String message = validationMessage.getMessage();
                if (message != null && !TextUtils.isEmpty(message) && !message.equals(previousMessage))
                {
                    THToast.show(validationMessage.getMessage());
                }
                previousMessage = message;
            }

            @Override public void onCompleted()
            {
            }

            @Override public void onError(Throwable e)
            {
                Timber.e(e, "Failed to listen to validation message");
            }
        };
    }

    @NonNull public Observable<Boolean> getFieldsValidObservable()
    {
        return Observable.combineLatest(
                emailValidator.getValidationMessageObservable(),
                passwordValidator.getValidationMessageObservable(),
                confirmPasswordValidator.getValidationMessageObservable(),
                displayNameValidator.getValidationMessageObservable(),
                new Func4<ValidationMessage, ValidationMessage, ValidationMessage, ValidationMessage, Boolean>()
                {
                    @Override public Boolean call(ValidationMessage emailValidation,
                            ValidationMessage passwordValidation,
                            ValidationMessage confirmPasswordValidation,
                            ValidationMessage displayNameValidation)
                    {
                        return emailValidation.getValidStatus().equals(ValidatedView.Status.VALID)
                                && passwordValidation.getValidStatus().equals(ValidatedView.Status.VALID)
                                && confirmPasswordValidation.getValidStatus().equals(ValidatedView.Status.VALID)
                                && displayNameValidation.getValidStatus().equals(ValidatedView.Status.VALID);
                    }
                }
        );
    }

    protected BitmapTypedOutput safeCreateProfilePhoto()
    {
        BitmapTypedOutput created = null;
        if (croppedPhotoFile != null)
        {
            try
            {
                Bitmap bitmap = BitmapFactory.decodeFile(croppedPhotoFile.getAbsolutePath());
                created = new BitmapTypedOutput(BitmapTypedOutput.TYPE_JPEG, bitmap, croppedPhotoFile.getAbsolutePath(), 75);
            }
            catch (OutOfMemoryError e)
            {
                THToast.show(R.string.error_decode_image_memory);
            }
        }
        return created;
    }

    public void populate(@NonNull UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
        firstName.setText(userProfileDTO.firstName);
        lastName.setText(userProfileDTO.lastName);
        displayNameValidator.setOriginalUsernameValue(userProfileDTO.displayName);
        displayNameValidator.setText(userProfileDTO.displayName);
        displayName.setText(userProfileDTO.displayName);
        referralCode.setText(userProfileDTO.inviteCode);
        if (userProfileDTO.inviteCode != null)
        {
            referralCode.setEnabled(false);
        }
        String currentEmail = email.getText().toString();
        if (currentEmail.isEmpty())
        {
            email.setText(userProfileDTO.email);
        }
        displayProfileImage();
    }

    //region Display user information
    public void displayProfileImage()
    {
        if (croppedPhotoFile != null)
        {
            Bitmap bitmap = BitmapFactory.decodeFile(croppedPhotoFile.getAbsolutePath());
            displayProfileImage(bitmap);
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
            picasso.load(userBaseDTO.picture)
                    .placeholder(R.drawable.superman_facebook)
                    .transform(userPhotoTransformation)
                    .into(profileImage);
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

    public void validate()
    {
        emailValidator.validate();
        passwordValidator.validate();
        confirmPasswordValidator.validate();
        displayNameValidator.validate();
    }

    private void populateCredentials()
    {
        Account[] accounts = accountManager.getAccountsByType(PARAM_ACCOUNT_TYPE);
        if (accounts != null && accounts.length > 0)
        {
            String emailValue = null, passwordValue = null;
            for (Account account : accounts)
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

            if (emailValue != null)
            {
                this.emailValidator.setText(emailValue);
            }
            this.email.setText(emailValue);
            if (passwordValue != null)
            {
                this.passwordValidator.setText(passwordValue);
            }
            this.password.setText(passwordValue);
            if (passwordValue != null)
            {
                this.confirmPasswordValidator.setText(passwordValue);
            }
            this.confirmPasswordValidator.setMainPassword(passwordValue);
            this.confirmPassword.setText(passwordValue);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.image_optional) @Optional
    protected void showImageFromDialog()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                R.layout.image_picker_item,
                new String[] {
                        getContext().getString(R.string.user_profile_choose_image_from_camera),
                        getContext().getString(R.string.user_profile_choose_image_from_library)
                });
        subscriptions.add(AlertDialogRxUtil.build(getContext())
                .setTitle(R.string.user_profile_choose_image_from_choice)
                .setNegativeButton(R.string.cancel)
                .setSingleChoiceItems(adapter, -1)
                .setCanceledOnTouchOutside(true)
                .build()
                .subscribe(
                        new Action1<OnDialogClickEvent>()
                        {
                            @Override public void call(OnDialogClickEvent event)
                            {
                                event.dialog.dismiss();
                                switch (event.which)
                                {
                                    case 0:
                                        onImageFromCameraRequested();
                                        break;
                                    case 1:
                                        onImageFromLibraryRequested();
                                        break;
                                }
                            }
                        },
                        new EmptyAction1<Throwable>()));
    }

    private void onImageFromCameraRequested()
    {
        PackageManager pm = getContext().getPackageManager();
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        List<ResolveInfo> handlerActivities = pm.queryIntentActivities(cameraIntent, 0);
        if (handlerActivities.size() > 0)
        {
            mCurrentPhotoFile = createImageFile();
            if (mCurrentPhotoFile == null)
            {
                THToast.show(R.string.error_save_image_in_external_storage);
                return;
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(mCurrentPhotoFile));
            Fragment currentFragment = dashboardNavigator.getCurrentFragment();
            if (currentFragment != null)
            {
                currentFragment.startActivityForResult(cameraIntent, REQUEST_CAMERA);
            }
        }
        else
        {
            THToast.show(R.string.device_no_camera);
        }
    }

    private File createImageFile()
    {
        String imageFileName = "JPEG_" + System.currentTimeMillis();
        Fragment currentFragment = dashboardNavigator.getCurrentFragment();
        if (currentFragment == null)
        {
            return null;
        }
        File storageDir = currentFragment.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try
        {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        }
        catch (IOException e)
        {
            Timber.d(e, "createImageFile");
            return null;
        }
        return image;
    }

    private void onImageFromLibraryRequested()
    {
        Intent libraryIntent = new Intent(Intent.ACTION_PICK);
        libraryIntent.setType("image/jpeg");
        Fragment currentFragment = dashboardNavigator.getCurrentFragment();
        if (currentFragment != null)
        {
            try
            {
                currentFragment.startActivityForResult(libraryIntent, REQUEST_GALLERY);
            }
            catch (ActivityNotFoundException e)
            {
                Timber.e(e, "Could not request gallery");
                THToast.show(R.string.error_launch_photo_library);
            }
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK
                && data != null)
        {
            startPhotoZoom(data.getData(), 150);
            currentRequest = REQUEST_GALLERY;
            return;
        }
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK)
        {
            startPhotoZoom(Uri.fromFile(mCurrentPhotoFile), 150);
            currentRequest = REQUEST_CAMERA;
            return;
        }
        if (requestCode == REQUEST_PHOTO_ZOOM && data != null)
        {
            Bundle bundle = data.getExtras();
            if (bundle != null)
            {
                Bitmap bitmap = bundle.getParcelable("data");
                if (saveBitmapToFile(bitmap)) return;

                if (currentRequest == REQUEST_CAMERA)
                {
                    currentRequest = -1;
                    profileImage.setImageBitmap(bitmap);
                    return;
                }
                if (currentRequest == REQUEST_GALLERY)
                {
                    currentRequest = -1;
                    profileImage.setImageBitmap(bitmap);
                    return;
                }
            }
            return;
        }
    }

    private boolean saveBitmapToFile(Bitmap bitmap)
    {
        croppedPhotoFile = createImageFile();
        if (croppedPhotoFile == null)
        {
            THToast.show(R.string.error_save_image_in_external_storage);
            return true;
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(croppedPhotoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
            outputStream.flush();
        } catch (Exception e) {
            THToast.show(R.string.error_save_image_in_external_storage);
            return true;
        } finally
        {
            if (outputStream != null) {
                try
                {
                    outputStream.close();
                }
                catch (IOException e)
                {
                    Timber.d(e, "Close");
                }
            }
        }
        return false;
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
        Fragment currentFragment = dashboardNavigator.getCurrentFragment();
        if (currentFragment != null)
        {
            currentFragment.startActivityForResult(intent, REQUEST_PHOTO_ZOOM);
        }
    }

    @NonNull public Observable<UserFormDTO> obtainUserFormDTO()
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
                new Func8<OnTextChangeEvent,
                        OnTextChangeEvent,
                        OnTextChangeEvent,
                        OnTextChangeEvent,
                        OnTextChangeEvent,
                        OnTextChangeEvent,
                        OnTextChangeEvent,
                        ImageView,
                        UserFormDTO>()
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
