package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.tradehero.common.utils.OnlineStateReceiver;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.auth.AuthDataUtil;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.MakePairFunc2;
import com.tradehero.th.rx.ToastAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.DeviceUtil;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class SettingsProfileFragment extends BaseFragment
{
    @InjectView(R.id.authentication_sign_up_button) protected Button updateButton;
    @InjectView(R.id.sign_up_form_wrapper) protected NotifyingScrollView scrollView;
    @InjectView(R.id.profile_info) protected ProfileInfoView profileView;
    @InjectView(R.id.authentication_sign_up_referral_code) protected EditText referralCodeEditText;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_settings_profile, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        updateButton.setText(R.string.update);
        referralCodeEditText.setVisibility(View.GONE);
        setHasOptionsMenu(true);
        try
        {
            view.setBackgroundResource(R.drawable.login_bg_4);
        } catch (Throwable e)
        {
            Timber.e(e, "Failed to set guide background");
            view.setBackgroundColor(getResources().getColor(R.color.authentication_guide_bg_color));
        }
    }

    @Override public void onStart()
    {
        super.onStart();
        populateCurrentUser();
    }

    @Override public void onDestroyView()
    {
        profileView = null;
        scrollView.setOnScrollChangedListener(null);
        updateButton = null;
        referralCodeEditText = null;
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @NonNull public Observable<Boolean> getFieldsValidObservable()
    {
        return profileView.getFieldsValidObservable();
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
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                userProfileCache.get().get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<UserBaseKey, UserProfileDTO>>()
                        {
                            @Override public void call(Pair<UserBaseKey, UserProfileDTO> pair)
                            {
                                profileView.populate(pair.second);
                            }
                        },
                        new ToastOnErrorAction()));
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.authentication_sign_up_button)
    protected void updateProfile(View view)
    {
        profileView.validate();
        DeviceUtil.dismissKeyboard(view);

        if (!OnlineStateReceiver.isOnline(getActivity()))
        {
            THToast.show(R.string.network_error);
        }
        else
        {
            onStopSubscriptions.add(AppObservable.bindFragment(
                    this,
                    getFieldsValidObservable()
                            .flatMap(new Func1<Boolean, Observable<Pair<AuthData, UserProfileDTO>>>()
                            {
                                @Override public Observable<Pair<AuthData, UserProfileDTO>> call(Boolean areFieldsValid)
                                {
                                    if (!areFieldsValid)
                                    {
                                        // Don't show the toast message, because we've already shown
                                        // invalid sign in profile view
                                        // THToast.show(R.string.validation_please_correct);
                                        return Observable.empty();
                                    }
                                    else
                                    {
                                        final ProgressDialog progressDialog = ProgressDialog.show(
                                                getActivity(),
                                                getString(R.string.alert_dialog_please_wait),
                                                getString(R.string.authentication_connecting_tradehero_only),
                                                true);
                                        progressDialog.setCancelable(true);

                                        return profileView.obtainUserFormDTO()
                                                .flatMap(new Func1<UserFormDTO, Observable<? extends Pair<AuthData, UserProfileDTO>>>()
                                                {
                                                    @Override public Observable<? extends Pair<AuthData, UserProfileDTO>> call(
                                                            UserFormDTO userFormDTO)
                                                    {
                                                        final AuthData authData = new AuthData(userFormDTO.email, userFormDTO.password);
                                                        Observable<UserProfileDTO> userProfileDTOObservable =
                                                                userServiceWrapper.get().updateProfileRx(currentUserId
                                                                        .toUserBaseKey(), userFormDTO);
                                                        return Observable.zip(
                                                                Observable.just(authData),
                                                                userProfileDTOObservable,
                                                                new MakePairFunc2<AuthData, UserProfileDTO>());
                                                    }
                                                })
                                                .doOnUnsubscribe(new DismissDialogAction0(progressDialog));
                                    }
                                }
                            }))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<Pair<AuthData, UserProfileDTO>>()
                            {
                                @Override public void call(Pair<AuthData, UserProfileDTO> pair)
                                {
                                    THToast.show(R.string.settings_update_profile_successful);
                                    AuthDataUtil.saveAccount(getActivity(), pair.first, pair.second.email);
                                    getActivity().finish();
                                }
                            },
                            new ToastAction<Throwable>(getString(R.string.error_update_your_user_profile))));
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
}



