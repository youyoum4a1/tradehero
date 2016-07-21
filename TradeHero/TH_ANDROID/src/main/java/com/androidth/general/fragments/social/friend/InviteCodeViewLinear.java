package com.androidth.general.fragments.social.friend;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.BindView;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;

import butterknife.Unbinder;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class InviteCodeViewLinear extends LinearLayout
{
    @Inject InvitedCodeViewHolder viewHolder;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;

    @BindView(R.id.btn_cancel) View cancelButton;
    @BindView(R.id.btn_send_code) View sendCodeButton;
    @BindView(R.id.btn_cancel_submit) View cancelSubmitButton;

    @Nullable private Subscription userProfileCacheSubscription;

    private Unbinder unbinder;

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
        unbinder = ButterKnife.bind(this);
        if (!isInEditMode())
        {
            viewHolder.attachView(this);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        unbinder = ButterKnife.bind(this);
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
        unbinder.unbind();
        super.onDetachedFromWindow();
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
