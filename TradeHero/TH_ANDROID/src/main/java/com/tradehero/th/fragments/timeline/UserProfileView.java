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
import com.tradehero.th.fragments.portfolio.PortfolioRequestListener;
import java.lang.ref.WeakReference;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/5/14 Time: 3:00 PM Copyright (c) TradeHero
 */
public class UserProfileView extends BetterViewAnimator
    implements DTOView<UserProfileDTO>
{
    @InjectView(R.id.user_profile_compact_view) @Optional protected UserProfileCompactView userProfileCompactView;
    @InjectView(R.id.user_profile_detail_view) @Optional protected UserProfileDetailView userProfileDetailView;

    private WeakReference<PortfolioRequestListener> portfolioRequestListener = new WeakReference<>(null);

    //region Constructors
    public UserProfileView(Context context)
    {
        super(context);
    }

    public UserProfileView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //endregion

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (userProfileCompactView != null)
        {
            userProfileCompactView.setPortfolioRequestListener(portfolioRequestListener.get());
        }
        if (userProfileDetailView != null)
        {
            userProfileDetailView.setPortfolioRequestListener(portfolioRequestListener.get());
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (userProfileCompactView != null)
        {
            userProfileCompactView.setPortfolioRequestListener(null);
        }
        if (userProfileDetailView != null)
        {
            userProfileDetailView.setPortfolioRequestListener(null);
        }
        super.onDetachedFromWindow();
    }

    @Override public void display(UserProfileDTO userProfileDTO)
    {
        if (userProfileCompactView != null)
        {
            userProfileCompactView.display(userProfileDTO);
        }
        if (userProfileDetailView != null)
        {
            userProfileDetailView.display(userProfileDTO);
        }
    }

    public void setPortfolioRequestListener(PortfolioRequestListener portfolioRequestListener)
    {
        this.portfolioRequestListener = new WeakReference<>(portfolioRequestListener);
        if (userProfileCompactView != null)
        {
            userProfileCompactView.setPortfolioRequestListener(portfolioRequestListener);
        }
        if (userProfileDetailView != null)
        {
            userProfileDetailView.setPortfolioRequestListener(portfolioRequestListener);
        }
    }
}
