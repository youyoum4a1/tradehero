package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th2.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UpdateReferralCodeDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class SettingsReferralCodeFragment extends DashboardFragment
{
    private MiddleCallback<Response> updateMiddleCallback;
    private ProgressDialog progressDialog;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtilLazy;
    @Inject Lazy<CurrentActivityHolder> currentActivityHolderLazy;

    @InjectView(R.id.btn_ok) Button mOKButton;
    @InjectView(R.id.btn_cancel) Button mCancelButton;
    @InjectView(R.id.referral_code) EditText mInviteCode;
    @InjectView(R.id.settings_referral_code) TextView mReferralCode;
    @InjectView(R.id.referral_code_dialog_key) LinearLayout mReferralCodeLayout;
    @InjectView(R.id.already_done_key) LinearLayout mAlreadyDoneLayout;

    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userProfileCacheListener = createProfileCacheListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings_referral_code_layout, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchProfile();
    }

    @Override public void onDestroyView()
    {
        detachProfileCache();
        detachUpdateMiddleCallback();
        if (progressDialog != null)
        {
            progressDialog.dismiss();
        }
        progressDialog = null;
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        userProfileCacheListener = null;
        super.onDestroy();
    }

    protected void fetchProfile()
    {
        detachProfileCache();
        userProfileCache.register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    protected void detachProfileCache()
    {
        userProfileCache.unregister(userProfileCacheListener);
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createProfileCacheListener()
    {
        return new SettingsReferralUserProfileListener();
    }

    protected class SettingsReferralUserProfileListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
            Timber.e("Failed to fetch profile info", error);
        }
    }

    private void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        if (userProfileDTO != null)
        {
            mReferralCode.setText(userProfileDTO.referralCode);
            mInviteCode.setText(userProfileDTO.inviteCode);
            if (userProfileDTO.inviteCode != null && !userProfileDTO.inviteCode.isEmpty())
            {
                mReferralCodeLayout.setVisibility(View.GONE);
                mAlreadyDoneLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.btn_cancel)
    protected void popFragment(View view)
    {
        getDashboardNavigator().popFragment();
    }

    @OnClick(R.id.btn_ok)
    protected void submitReferralCode(View view)
    {
        if (mInviteCode.getText().toString().length() > 5)
        {
            detachUpdateMiddleCallback();
            getProgressDialog().show();
            UpdateReferralCodeDTO updateReferralCodeDTO = new UpdateReferralCodeDTO(
                    mInviteCode.getText().toString());
            updateMiddleCallback = userServiceWrapperLazy.get().updateReferralCode(currentUserId.toUserBaseKey(), updateReferralCodeDTO, new TrackCallback());
        }
    }

    private class TrackCallback implements retrofit.Callback<Response>
    {
        @Override public void success(Response response, Response response2)
        {
            userProfileCache.invalidate(currentUserId.toUserBaseKey());
            getProgressDialog().dismiss();
            mReferralCodeLayout.setVisibility(View.GONE);
            mAlreadyDoneLayout.setVisibility(View.VISIBLE);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            getProgressDialog().dismiss();
        }
    }

    private void detachUpdateMiddleCallback()
    {
        if (updateMiddleCallback != null)
        {
            updateMiddleCallback.setPrimaryCallback(null);
        }
        updateMiddleCallback = null;
    }

    private ProgressDialog getProgressDialog()
    {
        if (progressDialog != null)
        {
            return progressDialog;
        }
        progressDialog = progressDialogUtilLazy.get().show(currentActivityHolderLazy.get().getCurrentActivity(), R.string.loading_loading,
                R.string.alert_dialog_please_wait);
        progressDialog.hide();
        return progressDialog;
    }
}
