package com.tradehero.th.fragments.portfolio.header;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.UserProfileDTO;

/**
 * Header displayed on a Portfolio owned by the authenticated user.
 */
public class CurrentUserFxPortfolioHeaderView extends CurrentUserPortfolioHeaderView
{
    @InjectView(R.id.header_portfolio_margin_available) protected TextView marginAvailable;
    @InjectView(R.id.header_portfolio_margin_used) protected TextView marginUsed;
    @InjectView(R.id.header_portfolio_pl_unrealised) protected TextView unrealisedPl;
    @InjectView(R.id.header_portfolio_pl_realised) protected TextView realisedPl;

    //<editor-fold desc="Constructors">
    public CurrentUserFxPortfolioHeaderView(Context context)
    {
        super(context);
    }

    public CurrentUserFxPortfolioHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CurrentUserFxPortfolioHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>
}
