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

/**
 * Created by thonguyen on 12/4/14.
 */
public class UpdateCenterResideMenuItem extends LinearLayout
        implements DTOView<UserProfileDTO>
{
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;

    @InjectView(R.id.tab_title_number) TextView unreadMessageCount;

    private UserProfileDTO userProfileDTO;
    private DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileListener;
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

        userProfileListener = new UserProfileFetchListener();
    }

    private void fetchAndDisplayUserProfile()
    {
        detachUserProfileFetchTask();
        userProfileFetchTask = userProfileCache.get()
                .getOrFetch(currentUserId.toUserBaseKey(), false, userProfileListener);
        userProfileFetchTask.execute();
    }

    @Override protected void onAttachedToWindow()
    {
        userProfileListener = new UserProfileFetchListener();
        fetchAndDisplayUserProfile();
        Timber.d("UpdateCenterResideMenuItem onAttachedToWindow fetchAndDisplayUserProfile");
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        detachUserProfileFetchTask();
        userProfileListener = null;
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
        updateUserProfileCache();
        linkWith(dto, true);
    }

    /**
     * update user profile cache
     */
    private void updateUserProfileCache()
    {
        // TODO synchronization problem
        UserBaseKey userBaseKey = currentUserId.toUserBaseKey();
        UserProfileDTO userProfileDTO = userProfileCache.get().get(currentUserId.toUserBaseKey());
        userProfileCache.get().put(userBaseKey, userProfileDTO);
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
