package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.portfolio.PortfolioRequestListener;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import java.lang.ref.WeakReference;

/** Created with IntelliJ IDEA. User: tho Date: 9/10/13 Time: 6:34 PM Copyright (c) TradeHero */
public class UserProfileDetailView extends LinearLayout implements DTOView<UserProfileDTO>, View.OnClickListener
{
    protected UserProfileDetailViewHolder userProfileDetailViewHolder;
    private WeakReference<PortfolioRequestListener> portfolioRequestListener = new WeakReference<>(null);

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
        initClickEvent();
    }


    private void initClickEvent()
    {
        userProfileDetailViewHolder.followersCount.setOnClickListener(this);
        userProfileDetailViewHolder.heroesCount.setOnClickListener(this);
    }

    @Override public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.user_profile_heroes_count:
                if (onHeroClickListener != null)
                {
                    onHeroClickListener.onHeroClick();
                }
                break;
            case R.id.user_profile_followers_count:
                if (onHeroClickListener != null)
                {
                    onHeroClickListener.onFollowerClick();
                }
                break;
        }
    }

    public static interface OnHeroClickListener
    {
        void onHeroClick();
        void onFollowerClick();
    }

    OnHeroClickListener onHeroClickListener;
    public void setOnHeroClickListener(OnHeroClickListener onHeroClickListener)
    {
        this.onHeroClickListener = onHeroClickListener;
    }



    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (userProfileDetailViewHolder != null)
        {
            userProfileDetailViewHolder.onAttachedToWindow();
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
    }

    @Override protected void onDetachedFromWindow()
    {
        if (userProfileDetailViewHolder != null)
        {
            userProfileDetailViewHolder.onDetachedFromWindow();
            if (userProfileDetailViewHolder.btnDefaultPortfolio != null)
            {
                userProfileDetailViewHolder.btnDefaultPortfolio.setOnClickListener(null);
            }
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

    /**
     * Listeners should be strongly referenced elsewhere
     * @param portfolioRequestListener
     */
    public void setPortfolioRequestListener(PortfolioRequestListener portfolioRequestListener)
    {
        this.portfolioRequestListener = new WeakReference<>(portfolioRequestListener);
    }

    private void notifyDefaultPortfolioRequested()
    {
        PortfolioRequestListener listener = portfolioRequestListener.get();
        if (listener != null)
        {
            listener.onDefaultPortfolioRequested();
        }
    }
}
