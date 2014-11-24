package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import retrofit.Callback;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class InviteCodeViewLinear extends LinearLayout
{
    @Inject InvitedCodeViewHolder viewHolder;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;

    @InjectView(R.id.btn_cancel) View cancelButton;
    @InjectView(R.id.btn_send_code) View sendCodeButton;
    @InjectView(R.id.btn_cancel_submit) View cancelSubmitButton;

    @Nullable private Subscription userProfileCacheSubscription;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration") public InviteCodeViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        if (!isInEditMode())
        {
            viewHolder.attachView(this);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        if (!isInEditMode())
        {
            viewHolder.attachView(this);
            fetchUserProfile();
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        userProfileCacheSubscription = null;
        detachUserProfileCache();
        viewHolder.detachView();
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void setParentCallback(@Nullable Callback<BaseResponseDTO> parentCallback)
    {
        viewHolder.setParentCallback(parentCallback);
    }

    protected void fetchUserProfile()
    {
        UserBaseKey key = currentUserId.toUserBaseKey();
        detachUserProfileCache();
        userProfileCacheSubscription = userProfileCache.get(key)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createUserProfileObserver());
    }

    private void detachUserProfileCache()
    {
        Subscription copy = userProfileCacheSubscription;
        if (copy != null)
        {
            copy.unsubscribe();
        }
        userProfileCacheSubscription = null;
    }

    protected Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileObserver()
    {
        return new InviteCodeViewUserProfileCacheObserver();
    }

    protected class InviteCodeViewUserProfileCacheObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            viewHolder.setUserProfile(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }
}
