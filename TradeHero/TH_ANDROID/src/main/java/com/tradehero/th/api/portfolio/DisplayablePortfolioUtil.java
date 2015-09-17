package com.tradehero.th.api.portfolio;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTOUtil;

public class DisplayablePortfolioUtil
{
    @NonNull public static String getLongTitle(
            @NonNull Resources resources,
            @Nullable DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        String title = null;
        if (displayablePortfolioDTO != null && displayablePortfolioDTO.portfolioDTO != null)
        {
            title = PortfolioDTOUtil.getLongTitle(resources,
                    displayablePortfolioDTO.portfolioDTO);
        }
        if (title != null)
        {
            return title;
        }
        return resources.getString(R.string.portfolio_title_unnamed);
    }

    public static int getLongTitleTextColor(
            @NonNull Resources resources,
            @Nullable DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        int colorRes;
        if (displayablePortfolioDTO instanceof DummyFxDisplayablePortfolioDTO)
        {
            colorRes = R.color.grey;
        }
        else
        {
            colorRes = R.color.black;
        }
        return resources.getColor(colorRes);
    }

    @Nullable public static String getLongSubTitle(
            @NonNull Resources resources,
            @NonNull CurrentUserId currentUserId,
            @Nullable DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        String subTitle = null;
        if (displayablePortfolioDTO instanceof DummyFxDisplayablePortfolioDTO)
        {
            subTitle = resources.getString(R.string.portfolio_tap_to_enroll_fx);
        }
        else if (displayablePortfolioDTO != null)
        {
            boolean isCurrentUser = displayablePortfolioDTO.userBaseDTO != null && currentUserId.toUserBaseKey().equals(displayablePortfolioDTO.userBaseDTO.getBaseKey());
            if (!isCurrentUser)
            {
                subTitle = PortfolioDTOUtil.getLongSubTitle(
                        resources,
                        displayablePortfolioDTO.portfolioDTO,
                        UserBaseDTOUtil.getLongDisplayName(resources, displayablePortfolioDTO.userBaseDTO));
            }
            else
            {
                subTitle = PortfolioDTOUtil.getLongSubTitle(
                        resources,
                        displayablePortfolioDTO.portfolioDTO);
            }
        }
        return subTitle;
    }
}
