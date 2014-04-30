package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserProfileDTO;

public class UserProfileCompactView extends RelativeLayout implements DTOView<UserProfileDTO>
{
    protected UserProfileCompactViewHolder userProfileCompactViewHolder;
    protected UserProfileDTO userProfileDTO;
    private UserProfileCompactViewHolder.OnProfileClickedListener profileClickedListener;

    //<editor-fold desc="Constructors">
    public UserProfileCompactView(Context context)
    {
        super(context);
    }

    public UserProfileCompactView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public UserProfileCompactView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        userProfileCompactViewHolder = new UserProfileCompactViewHolder(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        userProfileCompactViewHolder.initViews(this);
        userProfileCompactViewHolder.setProfileClickedListener(createProfileClickListener());
    }

    @Override protected void onDetachedFromWindow()
    {
        userProfileCompactViewHolder.detachViews();
        userProfileCompactViewHolder.setProfileClickedListener(null);
        super.onDetachedFromWindow();
    }

    public void setProfileClickedListener(
            UserProfileCompactViewHolder.OnProfileClickedListener profileClickedListener)
    {
        this.profileClickedListener = profileClickedListener;
    }

    @Override public void display(UserProfileDTO dto)
    {
        this.userProfileDTO = dto;
        if (userProfileCompactViewHolder != null)
        {
            userProfileCompactViewHolder.display(dto);
        }
    }

    private void notifyDefaultPortfolioRequested()
    {
        UserProfileCompactViewHolder.OnProfileClickedListener listener = profileClickedListener;
        if (listener != null)
        {
            listener.onDefaultPortfolioClicked();
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
        return new UserProfileCompactProfileClickedListener();
    }

    protected class UserProfileCompactProfileClickedListener implements UserProfileCompactViewHolder.OnProfileClickedListener
    {
        @Override public void onHeroClicked()
        {
            notifyHeroClicked();
        }

        @Override public void onFollowerClicked()
        {
            notifyFollowerClicked();
        }

        @Override public void onDefaultPortfolioClicked()
        {
            notifyDefaultPortfolioRequested();
        }
    }
}
