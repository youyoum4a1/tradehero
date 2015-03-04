package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.util.AttributeSet;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserProfileDTO;

public class UserProfileView extends BetterViewAnimator
    implements DTOView<UserProfileDTO>
{
    @InjectView(R.id.user_profile_detail_view) @Optional protected UserProfileDetailView userProfileDetailView;

    private UserProfileCompactViewHolder.OnProfileClickedListener profileClickedListener;

    //<editor-fold desc="Constructors">
    public UserProfileView(Context context)
    {
        super(context);
    }

    public UserProfileView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void setProfileClickedListener(
            UserProfileCompactViewHolder.OnProfileClickedListener profileClickedListener)
    {
        this.profileClickedListener = profileClickedListener;
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (userProfileDetailView != null)
        {
            userProfileDetailView.setProfileClickedListener(createProfileClickListener());
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (userProfileDetailView != null)
        {
            userProfileDetailView.setProfileClickedListener(null);
        }
        super.onDetachedFromWindow();
    }

    @Override public void display(UserProfileDTO userProfileDTO)
    {
        if (userProfileDetailView != null)
        {
            userProfileDetailView.display(userProfileDTO);
        }
    }

    private void notifyDefaultAchievementClicked()
    {
        UserProfileCompactViewHolder.OnProfileClickedListener listener = profileClickedListener;
        if (listener != null)
        {
            listener.onAchievementClicked();
        }
    }

    private void notifyHeroClicked()
    {
        UserProfileCompactViewHolder.OnProfileClickedListener listener = profileClickedListener;
        if (listener != null)
        {
            listener.onHeroClicked();
        }
    }

    private void notifyFollowerClicked()
    {
        UserProfileCompactViewHolder.OnProfileClickedListener listener = profileClickedListener;
        if (listener != null)
        {
            listener.onFollowerClicked();
        }
    }

    protected UserProfileCompactViewHolder.OnProfileClickedListener createProfileClickListener()
    {
        return new UserProfileClickedListener();
    }

    protected class UserProfileClickedListener implements UserProfileCompactViewHolder.OnProfileClickedListener
    {
        @Override public void onHeroClicked()
        {
            notifyHeroClicked();
        }

        @Override public void onFollowerClicked()
        {
            notifyFollowerClicked();
        }

        @Override public void onAchievementClicked()
        {
            notifyDefaultAchievementClicked();
        }
    }
}
