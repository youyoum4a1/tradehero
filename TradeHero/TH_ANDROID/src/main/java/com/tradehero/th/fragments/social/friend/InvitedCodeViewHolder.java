package com.ayondo.academy.fragments.social.friend;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ViewFlipper;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.ayondo.academy.R;
import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UpdateReferralCodeDTO;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.misc.exception.THException;
import com.ayondo.academy.network.service.UserServiceWrapper;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class InvitedCodeViewHolder
{
    public static final int VIEW_ENTER_CODE = 0;
    public static final int VIEW_SUBMITTING = 1;
    public static final int VIEW_SUBMIT_DONE = 2;

    @Bind(R.id.action_view_switcher) ViewFlipper viewSwitcher;
    @Bind(R.id.invite_code) EditText inviteCode;

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
        ButterKnife.bind(this, view);
        displayCurrentInviteCode();
    }

    public void detachView()
    {
        unsubscribe(updateInviteCodeSubscription);
        updateInviteCodeSubscription = null;
        ButterKnife.unbind(this);
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
                .subscribe(
                        new Action1<BaseResponseDTO>()
                        {
                            @Override public void call(BaseResponseDTO args)
                            {
                                InvitedCodeViewHolder.this.showSubmitDone();
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable e)
                            {
                                THException exception = new THException(e);
                                String message = exception.getMessage();
                                if (message != null && message.contains("Already invited"))
                                {
                                    InvitedCodeViewHolder.this.showSubmitDone();
                                }
                                else
                                {
                                    THToast.show(exception);
                                    viewSwitcher.setDisplayedChild(VIEW_ENTER_CODE);
                                }
                            }
                        });
    }

    public void showSubmitDone()
    {
        viewSwitcher.setDisplayedChild(VIEW_SUBMIT_DONE);
    }
}
