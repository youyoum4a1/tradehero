package com.tradehero.th.api.portfolio;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 12/2/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
public class DisplayablePortfolioUtil
{
    public static final String TAG = DisplayablePortfolioUtil.class.getSimpleName();

    @Inject public static CurrentUserId currentUserId;

    public static String getLongTitle(Context context, DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        if (displayablePortfolioDTO != null &&
                displayablePortfolioDTO.userBaseDTO != null &&
                !currentUserId.toUserBaseKey().equals(displayablePortfolioDTO.userBaseDTO.getBaseKey()))
        {
            return displayablePortfolioDTO.userBaseDTO.displayName;
        }

        if (displayablePortfolioDTO != null && displayablePortfolioDTO.portfolioDTO != null)
        {
            return displayablePortfolioDTO.portfolioDTO.title;
        }

        return context.getString(R.string.portfolio_item_title_loading);
    }
}
