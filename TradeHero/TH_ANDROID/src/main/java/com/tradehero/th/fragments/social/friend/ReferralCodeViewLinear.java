package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.client.Response;

public class ReferralCodeViewLinear extends LinearLayout
{
    @Inject ReferralCodeViewHolder viewHolder;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;

    @InjectView(R.id.btn_cancel) View cancelButton;
    @InjectView(R.id.btn_send_code) View sendCodeButton;
    @InjectView(R.id.btn_cancel_submit) View cancelSubmitButton;

    @Nullable private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;

    //<editor-fold desc="Constructors">
    public ReferralCodeViewLinear(Context context)
    {
        super(context);
        init();
    }

    public ReferralCodeViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ReferralCodeViewLinear(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    //</editor-fold>

    private void init()
    {
        DaggerUtils.inject(this);
    }

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
            userProfileCacheListener = createUserProfileListener();
            fetchUserProfile();
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        userProfileCacheListener = null;
        detachUserProfileCache();
        viewHolder.detachView();
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void setParentCallback(@Nullable Callback<Response> parentCallback)
    {
        viewHolder.setParentCallback(parentCallback);
    }

    protected void fetchUserProfile()
    {
        detachUserProfileCache();
        UserBaseKey key = currentUserId.toUserBaseKey();
        userProfileCache.register(key, userProfileCacheListener);
        userProfileCache.getOrFetchAsync(key);
    }

    private void detachUserProfileCache()
    {
        userProfileCache.unregister(userProfileCacheListener);
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileListener()
    {
        return new ReferralCodeViewUserProfileCacheListener();
    }

    protected class ReferralCodeViewUserProfileCacheListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            viewHolder.setUserProfile(value);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }
}
