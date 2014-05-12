package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserProfileDTO;

public class UserProfileDetailView extends LinearLayout implements DTOView<UserProfileDTO>
{
    protected UserProfileDetailViewHolder userProfileDetailViewHolder;
    private UserProfileCompactViewHolder.OnProfileClickedListener profileClickedListener;

    //<editor-fold desc="Constructors">
    public UserProfileDetailView(Context context)
    {
        this(context, null);
    }

    public UserProfileDetailView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public UserProfileDetailView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        userProfileDetailViewHolder = new UserProfileDetailViewHolder(this);
    }

    public void setProfileClickedListener(
            UserProfileCompactViewHolder.OnProfileClickedListener profileClickedListener)
    {
        this.profileClickedListener = profileClickedListener;
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        userProfileDetailViewHolder.initViews(this);
        userProfileDetailViewHolder.setProfileClickedListener(createProfileClickListener());
        if (userProfileDetailViewHolder.btnDefaultPortfolio != null)
        {
            userProfileDetailViewHolder.btnDefaultPortfolio.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    notifyDefaultPortfolioRequested();
                }
            });
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        userProfileDetailViewHolder.detachViews();
        userProfileDetailViewHolder.setProfileClickedListener(null);
        if (userProfileDetailViewHolder.btnDefaultPortfolio != null)
        {
            userProfileDetailViewHolder.btnDefaultPortfolio.setOnClickListener(null);
        }
        super.onDetachedFromWindow();
    }

    @Override public void setVisibility(int visibility)
    {
        super.setVisibility(visibility);

        if (userProfileDetailViewHolder != null)
        {
            userProfileDetailViewHolder.setVisibility(visibility);
        }
    }

    @Override public void display(final UserProfileDTO dto)
    {
        if (userProfileDetailViewHolder != null)
        {
            userProfileDetailViewHolder.display(dto);
        }
    }

    protected void notifyHeroClicked()
    {
        UserProfileCompactViewHolder.OnProfileClickedListener listener = profileClickedListener;
        if (listener != null)
        {
            listener.onHeroClicked();
        }
    }

    protected void notifyFollowerClicked()
    {
        UserProfileCompactViewHolder.OnProfileClickedListener listener = profileClickedListener;
        if (listener != null)
        {
            listener.onFollowerClicked();
        }
    }

    private void notifyDefaultPortfolioRequested()
    {
        UserProfileCompactViewHolder.OnProfileClickedListener  listener = profileClickedListener;
        if (listener != null)
        {
            listener.onDefaultPortfolioClicked();
        }
    }

    protected UserProfileCompactViewHolder.OnProfileClickedListener createProfileClickListener()
    {
        return new UserProfileDetailProfileClickedListener();
    }

    protected class UserProfileDetailProfileClickedListener implements UserProfileCompactViewHolder.OnProfileClickedListener
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
