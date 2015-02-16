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
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
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
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.graphics.BitmapTypedOutput;
import com.tradehero.th.models.graphics.BitmapTypedOutputFactory;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.GraphicUtil;
import com.tradehero.th.widget.MatchingPasswordText;
import com.tradehero.th.widget.ServerValidatedEmailText;
import com.tradehero.th.widget.ServerValidatedUsernameText;
import com.tradehero.th.widget.ValidatedPasswordText;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func8;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

import static com.tradehero.th.utils.Constants.Auth.PARAM_ACCOUNT_TYPE;

public class ProfileInfoView extends LinearLayout
        implements ActivityResultRequester
{
    public static final int REQUEST_GALLERY = 1309;
    public static final int REQUEST_CAMERA = 1310;

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
    @Inject DashboardNavigator dashboardNavigator;

    private UserProfileDTO userProfileDTO;
    private String newImagePath;
    @NonNull protected SubscriptionList subscriptions;

    //<editor-fold desc="Constructors">
    public ProfileInfoView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
        subscriptions = new SubscriptionList();
    }
    //</editor-fold>

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
        subscriptions.unsubscribe();
        subscriptions = new SubscriptionList();
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
                    .subscribe(
                            new EmptyAction1<OnDialogClickEvent>(),
                            new EmptyAction1<Throwable>());
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
            } catch (OutOfMemoryError e)
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

            this.email.setText(emailValue);
            this.password.setText(passwordValue);
            this.confirmPassword.setText(passwordValue);

            this.password.setValidateOnlyIfNotEmpty(passwordValue == null);
            this.confirmPassword.setValidateOnlyIfNotEmpty(passwordValue == null);
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
            //cameraIntent.setType("image/jpeg");
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
            } catch (ActivityNotFoundException e)
            {
                Timber.e(e, "Could not request gallery");
                THToast.show(R.string.error_launch_photo_library);
            }
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // handle image upload
        if (resultCode == Activity.RESULT_OK)
        {
            if ((requestCode == REQUEST_CAMERA || requestCode == REQUEST_GALLERY) && data != null)
            {
                try
                {
                    handleDataFromLibrary(data);
                } catch (OutOfMemoryError e)
                {
                    THToast.show(R.string.error_decode_image_memory);
                } catch (Exception e)
                {
                    THToast.show(R.string.error_fetch_image_library);
                    Timber.e(e, "Failed to extract image from library");
                }
            }
            else if (requestCode == REQUEST_GALLERY)
            {
                Timber.e(new Exception("Got null data from library"), "");
            }
        }
        else if (resultCode != Activity.RESULT_CANCELED)
        {
            Timber.e(new Exception("Failed to get image from library, resultCode: " + resultCode), "");
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
