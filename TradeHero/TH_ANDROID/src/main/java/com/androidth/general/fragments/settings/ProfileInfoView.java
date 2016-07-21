package com.androidth.general.fragments.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.androidth.general.R;
import com.androidth.general.api.form.UserFormDTO;
import com.androidth.general.api.users.UserBaseDTO;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.common.activities.ActivityResultRequester;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.graphics.BitmapTypedOutput;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.dialog.OnDialogClickEvent;
import com.androidth.general.utils.AlertDialogRxUtil;
import com.androidth.general.utils.GraphicUtil;
import com.androidth.general.widget.validation.DisplayNameValidatedText;
import com.androidth.general.widget.validation.DisplayNameValidator;
import com.androidth.general.widget.validation.EmailValidatedText;
import com.androidth.general.widget.validation.EmailValidator;
import com.androidth.general.widget.validation.PasswordValidatedText;
import com.androidth.general.widget.validation.TextValidator;
import com.androidth.general.widget.validation.ValidatedText;
import com.androidth.general.widget.validation.ValidatedView;
import com.androidth.general.widget.validation.ValidationMessage;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.Observer;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.functions.Func5;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

import static com.androidth.general.utils.Constants.Auth.PARAM_ACCOUNT_TYPE;

public class ProfileInfoView extends LinearLayout
        implements ActivityResultRequester
{
    private static final int INDEX_CHOICE_FROM_CAMERA = 0;
    private static final int INDEX_CHOICE_FROM_LIBRARY = 1;


    @BindView(R.id.authentication_sign_up_email) EmailValidatedText email;
    @BindView(R.id.authentication_sign_up_email_til) TextInputLayout email_til;
    EmailValidator emailValidator;



    @BindView(R.id.authentication_sign_up_password) PasswordValidatedText password;
    @BindView(R.id.authentication_sign_up_password_til) TextInputLayout password_til;
    TextValidator passwordValidator;
    TextWatcher targetPasswordWatcher;
    @BindView(R.id.authentication_sign_up_username) DisplayNameValidatedText displayName;
    @BindView(R.id.authentication_sign_up_username_til) TextInputLayout displayName_til;
    DisplayNameValidator displayNameValidator;
    @BindView(R.id.et_firstname) EditText firstName;
    @BindView(R.id.et_lastname) EditText lastName;
    @Inject Provider<UserFormDTO.Builder2> userFormBuilderProvider;

    @NonNull final AccountManager accountManager;
    private UserProfileDTO userProfileDTO;
    @NonNull protected SubscriptionList subscriptions;
    private ImageRequesterUtil imageRequesterUtil;

    private Unbinder unbinder;

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
        unbinder = ButterKnife.bind(this);
        emailValidator = email.getValidator();
        passwordValidator = password.getValidator();
        displayNameValidator = displayName.getValidator();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        unbinder = ButterKnife.bind(this);
        displayProfileImage();

        /*email.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("Email focus","Changed");
                if(!hasFocus && email.getText()!=null){
                    email_focus = true;
                }
                if(hasFocus){
                    //password_focus = false;
                    //display_name_focus = false;
                }
            }
        });
        password.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("Password focus","Changed");
                if(!hasFocus && password.getText()!=null){
                    password_focus = true;
                }
                if(hasFocus){
                    //email_focus = false;
                    //display_name_focus = false;
                }
            }
        });
        displayName.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("Username Focus","Changed");
                if(!hasFocus && displayName.getText()!=null){
                    display_name_focus = true;
                }
                if(hasFocus){
                    //email_focus = false;
                    //password_focus = false;
                }
            }
        });*/
        email.setOnFocusChangeListener(emailValidator);
        email.addTextChangedListener(emailValidator);
        password.setOnFocusChangeListener(passwordValidator);
        password.addTextChangedListener(passwordValidator);
        subscriptions.add(passwordValidator.getValidationMessageObservable().subscribe(createValidatorObserver(password)));
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
        subscriptions.add(emailValidator.getValidationMessageObservable().subscribe(createValidatorObserver(email)));
        subscriptions.add(displayNameValidator.getValidationMessageObservable().subscribe(createValidatorObserver(displayName)));
    }

    @Override protected void onDetachedFromWindow()
    {
        displayName.removeTextChangedListener(displayNameValidator);
        displayName.setOnFocusChangeListener(null);
        password.removeTextChangedListener(targetPasswordWatcher);
        targetPasswordWatcher = null;
        password.removeTextChangedListener(passwordValidator);
        password.setOnFocusChangeListener(null);
        email.removeTextChangedListener(emailValidator);
        email.setOnFocusChangeListener(null);
        subscriptions.unsubscribe();
        subscriptions = new SubscriptionList();
        unbinder.unbind();
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
                Log.i("Status",validationMessage.getValidStatus()+"");
                String message = validationMessage.getMessage();
                Log.i("Message",message+"");
                if (message != null && !TextUtils.isEmpty(message) && !message.equals(previousMessage))
                {

                    if(validatedText instanceof EmailValidatedText && emailValidator.isFocussedChanged() && emailValidator.isTextChanged()){
                        email_til.setError(validationMessage.getMessage());
                        emailValidator.setFocus(false);
                        emailValidator.setHasTextChanged(false);
                    }
                    else if(validatedText instanceof PasswordValidatedText && passwordValidator.isFocussedChanged() && passwordValidator.isTextChanged()){
                        password_til.setError(validationMessage.getMessage());
                        passwordValidator.setFocus(false);
                        passwordValidator.setHasTextChanged(false);
                    }
                    else if(validatedText instanceof DisplayNameValidatedText && displayNameValidator.isFocussedChanged() && displayNameValidator.isTextChanged()){
                        displayName_til.setError(validationMessage.getMessage());
                        displayNameValidator.setFocus(false);
                        displayNameValidator.setHasTextChanged(false);
                    }

                }
                else {
                    if(validatedText instanceof EmailValidatedText)
                        email_til.setError(null);
                    if(validatedText instanceof PasswordValidatedText)
                        password_til.setError(null);
                    if(validatedText instanceof DisplayNameValidatedText)
                        displayName_til.setError(null);
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
                displayNameValidator.getValidationMessageObservable(),
                new Func3<ValidationMessage, ValidationMessage, ValidationMessage, Boolean>()
                {
                    @Override public Boolean call(ValidationMessage emailValidation,
                            ValidationMessage passwordValidation,
                            ValidationMessage displayNameValidation)
                    {
                        return emailValidation.getValidStatus().equals(ValidatedView.Status.VALID)
                                && passwordValidation.getValidStatus().equals(ValidatedView.Status.VALID)
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
        /*referralCode.setText(userProfileDTO.inviteCode);
        if (userProfileDTO.inviteCode != null)
        {
            referralCode.setEnabled(false);
        }*/
        String currentEmail = email.getText().toString();
        //if (currentEmail.isEmpty())
        //{
            emailValidator.setOriginalEmailValue(userProfileDTO.email);
            emailValidator.setText(userProfileDTO.email);
            email.setText(userProfileDTO.email);
        //}
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
        /*if (this.profileImage != null)
        {
            profileImage.setImageBitmap(userPhotoTransformation.transform(newImage));
        }*/
    }

    public void displayProfileImage(UserBaseDTO userBaseDTO)
    {
        /*if (this.profileImage != null)
        {
            picasso.load(userBaseDTO.picture)
                    .placeholder(R.drawable.superman_facebook)
                    .transform(userPhotoTransformation)
                    .into(profileImage);
        }*/
    }

    public void displayDefaultProfileImage()
    {
        /*if (this.profileImage != null && picasso != null)
        {
            picasso.load(R.drawable.superman_facebook)
                    .transform(userPhotoTransformation)
                    .into(profileImage);
        }*/
    }
    //endregion

    public void validate()
    {
        emailValidator.validate();
        passwordValidator.validate();
        //confirmPasswordValidator.validate();
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
                //this.confirmPasswordValidator.setText(passwordValue);
            }
            //this.confirmPasswordValidator.setMainPassword(passwordValue);
            //this.confirmPassword.setText(passwordValue);
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
                                imageRequesterUtil.onImageFromCameraRequested((Activity) getContext(), ImageRequesterUtil.REQUEST_CAMERA);
                                break;
                            case INDEX_CHOICE_FROM_LIBRARY:
                                imageRequesterUtil.onImageFromLibraryRequested((Activity) getContext(), ImageRequesterUtil.REQUEST_GALLERY);
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
                                /*if (profileImage != null)
                                {
                                    profileImage.setImageBitmap(bitmap);
                                }*/
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
                WidgetObservable.text(displayName, true),
                WidgetObservable.text(firstName, true),
                WidgetObservable.text(lastName, true),
                new Func5<OnTextChangeEvent,
                        OnTextChangeEvent,
                        OnTextChangeEvent,
                        OnTextChangeEvent,
                        OnTextChangeEvent,
                        UserFormDTO>()
                {
                    @Override public UserFormDTO call(
                            OnTextChangeEvent serverValidatedEmailText,
                            OnTextChangeEvent validatedPasswordText,
                            OnTextChangeEvent serverValidatedUsernameText,
                            OnTextChangeEvent firstName1,
                            OnTextChangeEvent lastName1)
                    {
                        return userFormBuilderProvider.get()
                                .email(email.getText().toString())
                                .password(password.getText().toString())
                                .displayName(displayName.getText().toString())
                                .firstName(firstName.getText().toString())
                                .lastName(lastName.getText().toString())
                                .profilePicture(safeCreateProfilePhoto())
                                .build();
                    }
                }

        );
    }
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
