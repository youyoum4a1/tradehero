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

public class InvitedCodeViewHolder
{
    public static final int VIEW_ENTER_CODE = 0;
    public static final int VIEW_SUBMITTING = 1;
    public static final int VIEW_SUBMIT_DONE = 2;

    @InjectView(R.id.action_view_switcher) ViewFlipper viewSwitcher;
    @InjectView(R.id.invite_code) EditText inviteCode;

    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final UserServiceWrapper userServiceWrapper;
    @Nullable private UserProfileDTO userProfileDTO;

    @Nullable private Callback<Response> parentCallback;
    @Nullable private MiddleCallback<Response> middleCallbackUpdateInviteCode;

    //<editor-fold desc="Constructors">
    @Inject public InvitedCodeViewHolder(
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
        displayCurrentInviteCode();
    }

    public void detachView()
    {
        detachMiddleCallbackUpdateInvite();
        ButterKnife.reset(this);
    }

    private void detachMiddleCallbackUpdateInvite()
    {
        MiddleCallback<Response> middleCallbackCopy = middleCallbackUpdateInviteCode;
        if (middleCallbackCopy != null)
        {
            middleCallbackCopy.setPrimaryCallback(null);
        }
        middleCallbackUpdateInviteCode = null;
    }

    public void setUserProfile(@NotNull UserProfileDTO userProfile)
    {
        this.userProfileDTO = userProfile;
        displayCurrentInviteCode();
    }

    protected void displayCurrentInviteCode()
    {
        if (inviteCode != null && userProfileDTO != null)
        {
            inviteCode.setText(userProfileDTO.inviteCode);
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
    public void submitInviteCode()
    {
        viewSwitcher.setDisplayedChild(VIEW_SUBMITTING);
        detachMiddleCallbackUpdateInvite();
        UpdateReferralCodeDTO formDTO = new UpdateReferralCodeDTO(inviteCode.getText().toString());
        middleCallbackUpdateInviteCode = userServiceWrapper.updateReferralCode(currentUserId.toUserBaseKey(), formDTO, createUpdateInviteCallback());
    }

    protected Callback<Response> createUpdateInviteCallback()
    {
        return new InviteCodeUpdateInviteCallback();
    }

    protected class InviteCodeUpdateInviteCallback implements Callback<Response>
    {
        @Override public void success(Response response, Response response2)
        {
            showSubmitDone();
            notifyParentCallbackSuccess(response, response2);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            if ((new THException(retrofitError)).getMessage().contains("Already invited"))
            {
                showSubmitDone();
            }
            else
            {
                THToast.show(new THException(retrofitError));
                viewSwitcher.setDisplayedChild(VIEW_ENTER_CODE);
            }
            notifyParentCallbackFailure(retrofitError);
        }
    }

    public void showSubmitDone()
    {
        viewSwitcher.setDisplayedChild(VIEW_SUBMIT_DONE);
    }
}
