package com.tradehero.th.fragments.settings;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.tradehero.common.utils.OnlineStateReceiver;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.fragments.authentication.AuthDataAction;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.MakePairFunc2;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.widget.ValidationListener;
import com.tradehero.th.widget.ValidationMessage;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.observers.EmptyObserver;

public class SettingsProfileFragment extends DashboardFragment implements View.OnClickListener, ValidationListener
{
    @InjectView(R.id.authentication_sign_up_button) protected Button updateButton;
    @InjectView(R.id.sign_up_form_wrapper) protected NotifyingScrollView scrollView;
    @InjectView(R.id.profile_info) protected ProfileInfoView profileView;
    @InjectView(R.id.authentication_sign_up_referral_code) protected EditText referralCodeEditText;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject Provider<AuthDataAction> authDataActionProvider;

    @Nullable Subscription userProfileSubscription;
    @Nullable Subscription userProfileUpdateSubscription;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings_profile, container, false);
        initSetup(view);
        setHasOptionsMenu(true);
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

    @Override public void onStart()
    {
        super.onStart();
        populateCurrentUser();
    }

    @Override public void onStop()
    {
        unsubscribe(userProfileUpdateSubscription);
        userProfileUpdateSubscription = null;
        unsubscribe(userProfileSubscription);
        userProfileSubscription = null;
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
        unsubscribe(userProfileSubscription);
        userProfileSubscription = AndroidObservable.bindFragment(
                this,
                userProfileCache.get().get(currentUserId.toUserBaseKey()))
                .subscribe(createUserProfileCacheObserver());
    }

    private Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileCacheObserver()
    {
        return new Observer<Pair<UserBaseKey, UserProfileDTO>>()
        {
            @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
            {
                profileView.populate(pair.second);
            }

            @Override public void onCompleted()
            {
            }

            @Override public void onError(Throwable e)
            {
                THToast.show(new THException(e));
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

            unsubscribe(userProfileUpdateSubscription);
            userProfileUpdateSubscription = AndroidObservable.bindFragment(
                    this,
                    profileView.obtainUserFormDTO()
                            .flatMap(userFormDTO -> {
                                final AuthData authData = new AuthData(userFormDTO.email, userFormDTO.password);
                                Observable<UserProfileDTO> userProfileDTOObservable = userServiceWrapper.get().updateProfileRx(currentUserId
                                        .toUserBaseKey(), userFormDTO);
                                return Observable.zip(Observable.just(authData), userProfileDTOObservable, new MakePairFunc2<>());
                            }))
                    .doOnNext(pair -> {
                        profileView.progressDialog.hide(); // Before otherwise it is reset
                        THToast.show(R.string.settings_update_profile_successful);
                        authDataActionProvider.get().call(pair);
                        navigator.get().popFragment();
                    })
                    .doOnError(error -> THToast.show(R.string.error_update_your_user_profile))
                    .subscribe(new EmptyObserver<>());
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
        } catch (ActivityNotFoundException e)
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



