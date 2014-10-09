package com.tradehero.th.fragments.settings;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.OnlineStateReceiver;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.authentication.AuthDataAction;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.widget.ValidationListener;
import com.tradehero.th.widget.ValidationMessage;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.EmptyObserver;
import timber.log.Timber;

public class SettingsProfileFragment extends DashboardFragment implements View.OnClickListener, ValidationListener
{
    protected Button updateButton;
    private ProfileInfoView profileView;
    private EditText referralCodeEditText;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject DashboardNavigator navigator;
    @Inject Provider<AuthDataAction> authDataActionProvider;

    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    private Subscription updateProfileSubscription;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings_profile, container, false);

        initSetup(view);
        setHasOptionsMenu(true);

        populateCurrentUser();
        return view;
    }

    protected void initSetup(View view)
    {
        profileView = (ProfileInfoView) view.findViewById(R.id.profile_info);

        updateButton = (Button) view.findViewById(R.id.authentication_sign_up_button);
        updateButton.setText(R.string.update);
        updateButton.setOnClickListener(this);

        referralCodeEditText = (EditText) view.findViewById(R.id.authentication_sign_up_referral_code);
        referralCodeEditText.setVisibility(View.GONE);

        //signupButton.setOnTouchListener(this);
    }

    @Override public void onStop()
    {
        detachUserProfileCache();
        if (updateProfileSubscription != null && !updateProfileSubscription.isUnsubscribed())
        {
            updateProfileSubscription.unsubscribe();
        }
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        profileView = null;
        if (updateButton != null)
        {
            updateButton.setOnClickListener(null);
        }
        updateButton = null;
        referralCodeEditText = null;
        super.onDestroyView();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        detachUserProfileCache();
    }

    private void detachUserProfileCache()
    {
        if (userProfileCacheListener != null)
        {
            userProfileCache.get().unregister(userProfileCacheListener);
        }
        userProfileCacheListener = null;
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.authentication_sign_up_button:
                updateProfile(view);
                break;
            case R.id.image_optional:
                askImageFromLibrary();
                break;
        }
    }

    public boolean areFieldsValid()
    {
        return profileView.areFieldsValid();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
        {
            if ((requestCode == ImagePickerView.REQUEST_CAMERA || requestCode == ImagePickerView.REQUEST_GALLERY) && data != null)
            {
                try
                {
                    if (profileView != null)
                    {
                        profileView.handleDataFromLibrary(data);
                    }
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
            Timber.e(new Exception("Failed to get image from libray, resultCode: " + resultCode), "");
        }
    }

    private void populateCurrentUser()
    {
        detachUserProfileCache();
        userProfileCacheListener = createUserProfileCacheListener();
        userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>()
        {
            @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
            {
                profileView.populate(value);
            }

            @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
            {
                THToast.show(new THException(error));
            }
        };
    }

    private void updateProfile(View view)
    {
        DeviceUtil.dismissKeyboard(view);

        if (!OnlineStateReceiver.isOnline(getActivity()))
        {
            THToast.show(R.string.network_error);
        }
        else if (!areFieldsValid())
        {
            THToast.show(R.string.validation_please_correct);
        }
        else
        {
            profileView.progressDialog = progressDialogUtil.show(
                    getActivity(),
                    R.string.alert_dialog_please_wait,
                    R.string.authentication_connecting_tradehero_only);
            profileView.progressDialog.setCancelable(true);

            updateProfileSubscription = profileView.obtainUserFormDTO()
                    .flatMap(new Func1<UserFormDTO, Observable<UserProfileDTO>>()
                    {
                        @Override public Observable<UserProfileDTO> call(UserFormDTO userFormDTO)
                        {
                            return userServiceWrapper.get().updateProfileRx(currentUserId.toUserBaseKey(), userFormDTO);
                        }
                    })
                    .doOnNext(new Action1<UserProfileDTO>()
                    {
                        @Override public void call(UserProfileDTO userProfileDTO)
                        {
                            profileView.progressDialog.hide(); // Before otherwise it is reset
                            THToast.show(R.string.settings_update_profile_successful);
                            navigator.popFragment();
                        }
                    })
                    // FIXME/refactor create or update account in AccountManager with email & password
                    .subscribe(new EmptyObserver<UserProfileDTO>());
        }
    }

    public String getPath(Uri uri)
    {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    protected void askImageFromLibrary()
    {
        Intent libraryIntent = new Intent(Intent.ACTION_PICK);
        libraryIntent.setType("image/jpeg");
        try
        {
            startActivityForResult(libraryIntent, ImagePickerView.REQUEST_GALLERY);
        }
        catch (ActivityNotFoundException e)
        {
            THToast.show(R.string.error_launch_photo_library);
        }
    }

    @Override public void notifyValidation(ValidationMessage message)
    {
        if (message != null && !message.getStatus() && message.getMessage() != null)
        {
            THToast.show(message.getMessage());
        }
    }
}



