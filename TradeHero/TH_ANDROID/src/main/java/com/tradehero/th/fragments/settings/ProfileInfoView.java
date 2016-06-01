package com.ayondo.academy.fragments.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.activities.ActivityResultRequester;
import com.tradehero.common.utils.THToast;
import com.ayondo.academy.R;
import com.ayondo.academy.api.form.UserFormDTO;
import com.ayondo.academy.api.users.UserBaseDTO;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.models.graphics.BitmapTypedOutput;
import com.ayondo.academy.models.graphics.ForUserPhoto;
import com.ayondo.academy.rx.TimberOnErrorAction1;
import com.ayondo.academy.rx.dialog.OnDialogClickEvent;
import com.ayondo.academy.utils.AlertDialogRxUtil;
import com.ayondo.academy.utils.GraphicUtil;
import com.ayondo.academy.widget.validation.DisplayNameValidatedText;
import com.ayondo.academy.widget.validation.DisplayNameValidator;
import com.ayondo.academy.widget.validation.MatchingPasswordText;
import com.ayondo.academy.widget.validation.PasswordConfirmTextValidator;
import com.ayondo.academy.widget.validation.PasswordValidatedText;
import com.ayondo.academy.widget.validation.TextValidator;
import com.ayondo.academy.widget.validation.ValidatedText;
import com.ayondo.academy.widget.validation.ValidatedView;
import com.ayondo.academy.widget.validation.ValidationMessage;
import java.io.File;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.Observer;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func4;
import rx.functions.Func8;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

import static com.ayondo.academy.utils.Constants.Auth.PARAM_ACCOUNT_TYPE;

public class ProfileInfoView extends LinearLayout
        implements ActivityResultRequester
{
    private static final int INDEX_CHOICE_FROM_CAMERA = 0;
    private static final int INDEX_CHOICE_FROM_LIBRARY = 1;

    @Bind(R.id.authentication_sign_up_email) ValidatedText email;
    TextValidator emailValidator;
    @Bind(R.id.authentication_sign_up_password) PasswordValidatedText password;
    TextValidator passwordValidator;
    @Bind(R.id.authentication_sign_up_confirm_password) MatchingPasswordText confirmPassword;
    PasswordConfirmTextValidator confirmPasswordValidator;
    TextWatcher targetPasswordWatcher;
    @Bind(R.id.authentication_sign_up_username) DisplayNameValidatedText displayName;
    DisplayNameValidator displayNameValidator;
    @Bind(R.id.authentication_sign_up_referral_code) EditText referralCode;
    @Bind(R.id.et_firstname) EditText firstName;
    @Bind(R.id.et_lastname) EditText lastName;
    @Bind(R.id.image_optional) @Nullable ImageView profileImage;

    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;
    @Inject Provider<UserFormDTO.Builder2> userFormBuilderProvider;

    @NonNull final AccountManager accountManager;
    private UserProfileDTO userProfileDTO;
    @NonNull protected SubscriptionList subscriptions;
    private ImageRequesterUtil imageRequesterUtil;

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
        ButterKnife.bind(this);
        emailValidator = email.getValidator();
        passwordValidator = password.getValidator();
        confirmPasswordValidator = confirmPassword.getValidator();
        displayNameValidator = displayName.getValidator();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
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
        ButterKnife.unbind(this);
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
        File croppedPhotoFile = imageRequesterUtil == null ? null : imageRequesterUtil.getCroppedPhotoFile();
        if (croppedPhotoFile != null)
        {
            try
            {
                created = GraphicUtil.fromFile(croppedPhotoFile);
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
        File croppedPhotoFile = imageRequesterUtil == null ? null : imageRequesterUtil.getCroppedPhotoFile();
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
    @OnClick(R.id.image_optional) @Nullable
    protected void showImageFromDialog()
    {
        String[] choices = new String[2];
        choices[INDEX_CHOICE_FROM_CAMERA] = getContext().getString(R.string.user_profile_choose_image_from_camera);
        choices[INDEX_CHOICE_FROM_LIBRARY] = getContext().getString(R.string.user_profile_choose_image_from_library);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                R.layout.image_picker_item,
                choices);
        imageRequesterUtil = new ImageRequesterUtil(1, 1, 150, 150);
        subscriptions.add(AlertDialogRxUtil.build(getContext())
                .setTitle(R.string.user_profile_choose_image_from_choice)
                .setNegativeButton(R.string.cancel)
                .setSingleChoiceItems(adapter, -1)
                .setCanceledOnTouchOutside(true)
                .build()
                .flatMap(new Func1<OnDialogClickEvent, Observable<Bitmap>>()
                {
                    @Override public Observable<Bitmap> call(OnDialogClickEvent event)
                    {
                        event.dialog.dismiss();
                        switch (event.which)
                        {
                            case INDEX_CHOICE_FROM_CAMERA:
                                imageRequesterUtil.onImageFromCameraRequested((Activity) getContext());
                                break;
                            case INDEX_CHOICE_FROM_LIBRARY:
                                imageRequesterUtil.onImageFromLibraryRequested((Activity) getContext());
                                break;
                        }
                        return imageRequesterUtil.getBitmapObservable();
                    }
                })
                .subscribe(
                        new Action1<Bitmap>()
                        {
                            @Override public void call(Bitmap bitmap)
                            {
                                if (profileImage != null)
                                {
                                    profileImage.setImageBitmap(bitmap);
                                }
                            }
                        },
                        new TimberOnErrorAction1("Failed to ask for and get bitmap")));
    }

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        if (imageRequesterUtil != null)
        {
            imageRequesterUtil.onActivityResult(activity, requestCode, resultCode, data);
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
