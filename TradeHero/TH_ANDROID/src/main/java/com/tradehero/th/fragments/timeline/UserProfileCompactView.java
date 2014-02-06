package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.portfolio.PortfolioRequestListener;
import java.lang.ref.WeakReference;

/** Created with IntelliJ IDEA. User: tho Date: 10/17/13 Time: 12:51 PM Copyright (c) TradeHero */
public class UserProfileCompactView extends RelativeLayout implements DTOView<UserProfileDTO>
{
    protected UserProfileCompactViewHolder userProfileCompactViewHolder;
    protected UserProfileDTO userProfileDTO;
    private WeakReference<PortfolioRequestListener> portfolioRequestListener = new WeakReference<>(null);

    //<editor-fold desc="Constructors">
    public UserProfileCompactView(Context context)
    {
        this(context, null);
    }

    public UserProfileCompactView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
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
        if (userProfileCompactViewHolder != null && userProfileCompactViewHolder.btnDefaultPortfolio != null)
        {
            userProfileCompactViewHolder.btnDefaultPortfolio.setOnClickListener(new OnClickListener()
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
        if (userProfileCompactViewHolder != null && userProfileCompactViewHolder.btnDefaultPortfolio != null)
        {
            userProfileCompactViewHolder.btnDefaultPortfolio.setOnClickListener(null);
        }
        super.onDetachedFromWindow();
    }

    @Override public void display(UserProfileDTO dto)
    {
        this.userProfileDTO = dto;
        if (userProfileCompactViewHolder != null)
        {
            userProfileCompactViewHolder.display(dto);
        }
    }

    /**
     * Listeners should be strongly referenced elsewhere
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
