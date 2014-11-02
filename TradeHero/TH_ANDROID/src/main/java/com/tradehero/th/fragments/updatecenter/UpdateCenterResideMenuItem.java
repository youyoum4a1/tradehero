package com.tradehero.th.fragments.updatecenter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class UpdateCenterResideMenuItem extends LinearLayout
        implements DTOView<UserProfileDTO>
{
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;

    @InjectView(R.id.tab_title_number) TextView unreadMessageCount;

    private Subscription userProfileCacheSubscription;

    //<editor-fold desc="Constructors">
    public UpdateCenterResideMenuItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
    }

    private void fetchAndDisplayUserProfile()
    {
        detachUserProfileCache();
        userProfileCacheSubscription = userProfileCache.get().get(currentUserId.toUserBaseKey())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createUserProfileFetchObserver());
    }

    @Override protected void onAttachedToWindow()
    {
        fetchAndDisplayUserProfile();
        Timber.d("UpdateCenterResideMenuItem onAttachedToWindow fetchAndDisplayUserProfile");
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        detachUserProfileCache();
        super.onDetachedFromWindow();
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

    @Override public void display(UserProfileDTO dto)
    {
        linkWith(dto, true);
    }

    private void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        if (userProfileDTO != null && andDisplay)
        {
            int totalUnreadItem = userProfileDTO.unreadMessageThreadsCount;
            unreadMessageCount.setText("" + totalUnreadItem);
            unreadMessageCount.setVisibility(totalUnreadItem == 0 ? GONE : VISIBLE);
        }
    }

    protected Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileFetchObserver()
    {
        return new UserProfileFetchObserver();
    }

    private class UserProfileFetchObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            display(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(new THException(e));
        }
    }
}
