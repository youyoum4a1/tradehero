package com.tradehero.th.fragments.updatecenter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

public class UpdateCenterResideMenuItem extends LinearLayout
        implements DTOView<UserProfileDTO>
{
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;

    @InjectView(R.id.tab_title_number) TextView unreadMessageCount;

    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileFetchTask;

    //<editor-fold desc="Constructors">
    public UpdateCenterResideMenuItem(Context context)
    {
        super(context);
    }

    public UpdateCenterResideMenuItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public UpdateCenterResideMenuItem(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        DaggerUtils.inject(this);
    }

    private void fetchAndDisplayUserProfile()
    {
        detachUserProfileFetchTask();
        userProfileFetchTask = userProfileCache.get()
                .getOrFetch(currentUserId.toUserBaseKey(), false, createUserProfileFetchListener());
        userProfileFetchTask.execute();
    }

    @Override protected void onAttachedToWindow()
    {
        fetchAndDisplayUserProfile();
        Timber.d("UpdateCenterResideMenuItem onAttachedToWindow fetchAndDisplayUserProfile");
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        detachUserProfileFetchTask();
        super.onDetachedFromWindow();
    }

    private void detachUserProfileFetchTask()
    {
        if (userProfileFetchTask != null)
        {
            userProfileFetchTask.setListener(null);
        }
        userProfileFetchTask = null;
    }

    @Override public void display(UserProfileDTO dto)
    {
        linkWith(dto, true);
    }

    private void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        if (userProfileDTO != null && andDisplay)
        {
            int totalUnreadItem = /*userProfileDTO.unreadNotificationsCount +*/
                    userProfileDTO.unreadMessageThreadsCount;
            Timber.d("UpdateCenterResideMenuItem totalUnread count %d,allFollowerCount:%d",
                    totalUnreadItem, userProfileDTO.allFollowerCount);
            unreadMessageCount.setText("" + totalUnreadItem);
            unreadMessageCount.setVisibility(totalUnreadItem == 0 ? GONE : VISIBLE);
        }
    }

    protected DTOCache.Listener<UserBaseKey, UserProfileDTO> createUserProfileFetchListener()
    {
        return new UserProfileFetchListener();
    }

    private class UserProfileFetchListener implements DTOCache.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override
        public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
        {
            display(value);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THToast.show(new THException(error));
        }
    }
}
