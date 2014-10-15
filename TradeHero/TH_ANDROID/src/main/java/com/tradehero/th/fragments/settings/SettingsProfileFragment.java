package com.tradehero.th.fragments.settings;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.OnlineStateReceiver;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.authentication.AuthDataAction;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.rx.MakePairFunc2;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.widget.ValidationListener;
import com.tradehero.th.widget.ValidationMessage;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class SettingsProfileFragment extends DashboardFragment implements View.OnClickListener, ValidationListener
{
    @InjectView(R.id.authentication_sign_up_button) protected Button updateButton;
    @InjectView(R.id.sign_up_form_wrapper) protected NotifyingScrollView scrollView;
    @InjectView(R.id.profile_info) protected ProfileInfoView profileView;
    @InjectView(R.id.authentication_sign_up_referral_code) protected EditText referralCodeEditText;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject DashboardNavigator navigator;
    @Inject Provider<AuthDataAction> authDataActionProvider;

    @Nullable private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
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
        ButterKnife.inject(this, view);

        updateButton.setText(R.string.update);
        updateButton.setOnClickListener(this);

        referralCodeEditText.setVisibility(View.GONE);

        scrollView.setOnScrollChangedListener(dashboardBottomTabScrollViewScrollListener.get());
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
        scrollView.setOnScrollChangedListener(null);
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
        if (profileView != null)
        {
            profileView.onActivityResult(requestCode, resultCode, data);
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
                    .flatMap(new Func1<UserFormDTO, Observable<Pair<AuthData, UserProfileDTO>>>()
                    {
                        @Override public Observable<Pair<AuthData, UserProfileDTO>> call(UserFormDTO userFormDTO)
                        {
                            final AuthData authData = new AuthData(userFormDTO.email, userFormDTO.password);
                            Observable<UserProfileDTO> userProfileDTOObservable = userServiceWrapper.get().updateProfileRx(currentUserId
                                .toUserBaseKey(), userFormDTO);
                            return Observable.zip(Observable.just(authData), userProfileDTOObservable, new MakePairFunc2<AuthData, UserProfileDTO>());
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<Pair<AuthData, UserProfileDTO>>()
                    {
                        @Override public void call(Pair<AuthData, UserProfileDTO> userProfileDTO)
                        {
                            profileView.progressDialog.hide(); // Before otherwise it is reset
                            THToast.show(R.string.settings_update_profile_successful);
                            navigator.popFragment();
                        }
                    })
                    .subscribe(authDataActionProvider.get());
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



