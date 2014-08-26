package com.tradehero.th.fragments.social.friend;

import android.view.View;
import android.widget.EditText;
import android.widget.ViewFlipper;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UpdateReferralCodeDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ReferralCodeViewHolder
{
    public static final int VIEW_ENTER_CODE = 0;
    public static final int VIEW_SUBMITTING = 1;
    public static final int VIEW_DONE = 2;

    @InjectView(R.id.action_view_switcher) ViewFlipper viewSwitcher;
    @InjectView(R.id.referral_code) EditText referralCode;

    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final UserServiceWrapper userServiceWrapper;
    @Nullable private UserProfileDTO userProfileDTO;

    @Nullable private Callback<Response> parentCallback;
    @Nullable private MiddleCallback<Response> middleCallbackUpdateReferral;

    //<editor-fold desc="Constructors">
    @Inject public ReferralCodeViewHolder(
            @NotNull CurrentUserId currentUserId,
            @NotNull UserServiceWrapper userServiceWrapper)
    {
        this.currentUserId = currentUserId;
        this.userServiceWrapper = userServiceWrapper;
    }
    //</editor-fold>

    public void attachView(View view)
    {
        ButterKnife.inject(this, view);
        displayCurrentReferralCode();
    }

    public void detachView()
    {
        detachMiddleCallbackUpdateReferral();
        ButterKnife.reset(this);
    }

    private void detachMiddleCallbackUpdateReferral()
    {
        MiddleCallback<Response> middleCallbackCopy = middleCallbackUpdateReferral;
        if (middleCallbackCopy != null)
        {
            middleCallbackCopy.setPrimaryCallback(null);
        }
        middleCallbackUpdateReferral = null;
    }

    public void setUserProfile(@NotNull UserProfileDTO userProfile)
    {
        this.userProfileDTO = userProfile;
        displayCurrentReferralCode();
    }

    protected void displayCurrentReferralCode()
    {
        if (referralCode != null && userProfileDTO != null)
        {
            referralCode.setText(userProfileDTO.inviteCode);
        }
    }

    public void setParentCallback(@Nullable Callback<Response> parentCallback)
    {
        this.parentCallback = parentCallback;
    }

    protected void notifyParentCallbackSuccess(Response response, Response response2)
    {
        Callback<Response> callbackCopy = parentCallback;
        if (callbackCopy != null)
        {
            callbackCopy.success(response, response2);
        }
    }

    protected void notifyParentCallbackFailure(RetrofitError retrofitError)
    {
        Callback<Response> callbackCopy = parentCallback;
        if (callbackCopy != null)
        {
            callbackCopy.failure(retrofitError);
        }
    }

    @OnClick(R.id.btn_send_code)
    public void submitReferralCode()
    {
        viewSwitcher.setDisplayedChild(VIEW_SUBMITTING);
        detachMiddleCallbackUpdateReferral();
        UpdateReferralCodeDTO formDTO = new UpdateReferralCodeDTO(referralCode.getText().toString());
        middleCallbackUpdateReferral = userServiceWrapper.updateReferralCode(currentUserId.toUserBaseKey(), formDTO, createUpdateReferralCallback());
    }

    protected Callback<Response> createUpdateReferralCallback()
    {
        return new ReferralCodeUpdateReferralCallback();
    }

    protected class ReferralCodeUpdateReferralCallback implements Callback<Response>
    {
        @Override public void success(Response response, Response response2)
        {
            showDone();
            notifyParentCallbackSuccess(response, response2);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            if ((new THException(retrofitError)).getMessage().contains("Already invited"))
            {
                showDone();
            }
            else
            {
                THToast.show(new THException(retrofitError));
                viewSwitcher.setDisplayedChild(VIEW_ENTER_CODE);
            }
            notifyParentCallbackFailure(retrofitError);
        }
    }

    public void showDone()
    {
        viewSwitcher.setDisplayedChild(VIEW_DONE);
    }
}
