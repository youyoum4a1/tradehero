package com.tradehero.th.fragments.social.friend;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ViewFlipper;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UpdateReferralCodeDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserServiceWrapper;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.EmptyObserver;

public class InvitedCodeViewHolder
{
    public static final int VIEW_ENTER_CODE = 0;
    public static final int VIEW_SUBMITTING = 1;
    public static final int VIEW_SUBMIT_DONE = 2;

    @InjectView(R.id.action_view_switcher) ViewFlipper viewSwitcher;
    @InjectView(R.id.invite_code) EditText inviteCode;

    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final UserServiceWrapper userServiceWrapper;
    @Nullable private UserProfileDTO userProfileDTO;

    @Nullable private Subscription updateInviteCodeSubscription;

    //<editor-fold desc="Constructors">
    @Inject public InvitedCodeViewHolder(
            @NonNull CurrentUserId currentUserId,
            @NonNull UserServiceWrapper userServiceWrapper)
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
        unsubscribe(updateInviteCodeSubscription);
        updateInviteCodeSubscription = null;
        ButterKnife.reset(this);
    }

    private void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    public void setUserProfile(@NonNull UserProfileDTO userProfile)
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

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.btn_send_code)
    public void submitInviteCode()
    {
        viewSwitcher.setDisplayedChild(VIEW_SUBMITTING);
        unsubscribe(updateInviteCodeSubscription);
        UpdateReferralCodeDTO formDTO = new UpdateReferralCodeDTO(inviteCode.getText().toString());
        updateInviteCodeSubscription = userServiceWrapper.updateReferralCodeRx(currentUserId.toUserBaseKey(), formDTO)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createUpdateInviteObserver());
    }

    protected Observer<BaseResponseDTO> createUpdateInviteObserver()
    {
        return new InviteCodeUpdateInviteObserver();
    }

    protected class InviteCodeUpdateInviteObserver extends EmptyObserver<BaseResponseDTO>
    {
        @Override public void onNext(BaseResponseDTO args)
        {
            showSubmitDone();
        }

        @Override public void onError(Throwable e)
        {
            THException exception = new THException(e);
            String message = exception.getMessage();
            if (message != null && message.contains("Already invited"))
            {
                showSubmitDone();
            }
            else
            {
                THToast.show(exception);
                viewSwitcher.setDisplayedChild(VIEW_ENTER_CODE);
            }
        }
    }

    public void showSubmitDone()
    {
        viewSwitcher.setDisplayedChild(VIEW_SUBMIT_DONE);
    }
}
