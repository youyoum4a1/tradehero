package com.tradehero.th.api.portfolio;

import android.content.Context;
import com.tradehero.thm.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import dagger.Lazy;
import javax.inject.Inject;

public class DisplayablePortfolioUtil
{
    @Inject public CurrentUserId currentUserId;
    @Inject public Lazy<PortfolioDTOUtil> portfolioDTOUtil;
    @Inject public Lazy<UserBaseDTOUtil> userBaseDTOUtil;

    @Inject public DisplayablePortfolioUtil()
    {
        super();
    }

    public String getLongTitle(Context context, DisplayablePortfolioDTO displayablePortfolioDTO)
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

    public String getLongTitleType(Context context, DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        if (displayablePortfolioDTO != null && displayablePortfolioDTO.portfolioDTO != null)
        {
            return portfolioDTOUtil.get().getLongTitleType(context,
                    displayablePortfolioDTO.portfolioDTO);
        }
        return context.getString(R.string.portfolio_title_unnamed);
    }

    public String getLongSubTitle(Context context, DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        String subTitle = null;
        if (displayablePortfolioDTO != null)
        {
            boolean isCurrentUser = displayablePortfolioDTO.userBaseDTO != null && currentUserId.toUserBaseKey().equals(displayablePortfolioDTO.userBaseDTO.getBaseKey());
            if (!isCurrentUser)
            {
                subTitle = portfolioDTOUtil.get().getLongSubTitle(
                        context,
                        displayablePortfolioDTO.portfolioDTO,
                        userBaseDTOUtil.get().getLongDisplayName(context, displayablePortfolioDTO.userBaseDTO));
            }
            else
            {
                subTitle = portfolioDTOUtil.get().getLongSubTitle(
                        context,
                        displayablePortfolioDTO.portfolioDTO);
            }
        }
        return subTitle;
    }
}
