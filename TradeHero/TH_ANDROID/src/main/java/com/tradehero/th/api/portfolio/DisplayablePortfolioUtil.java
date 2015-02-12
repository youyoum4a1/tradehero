package com.tradehero.th.api.portfolio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTOUtil;

public class DisplayablePortfolioUtil
{
    @NonNull public static String getLongTitle(
            @NonNull Context context,
            @Nullable DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        String title = null;
        if (displayablePortfolioDTO != null && displayablePortfolioDTO.portfolioDTO != null)
        {
            title = PortfolioDTOUtil.getLongTitle(context,
                    displayablePortfolioDTO.portfolioDTO);
        }
        if (title != null)
        {
            return title;
        }
        return context.getString(R.string.portfolio_title_unnamed);
    }

    public static int getLongTitleTextColor(
            @NonNull Context context,
            @Nullable DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        int colorRes;
        if (displayablePortfolioDTO instanceof DummyFxDisplayablePortfolioDTO)
        {
            colorRes = R.color.light_grey;
        }
        else
        {
            colorRes = R.color.black;
        }
        return context.getResources().getColor(colorRes);
    }

    @Nullable public static String getLongSubTitle(
            @NonNull Context context,
            @NonNull CurrentUserId currentUserId,
            @Nullable DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        String subTitle = null;
        if (displayablePortfolioDTO instanceof DummyFxDisplayablePortfolioDTO)
        {
            subTitle = context.getString(R.string.portfolio_tap_to_enroll_fx);
        }
        else if (displayablePortfolioDTO != null)
        {
            boolean isCurrentUser = displayablePortfolioDTO.userBaseDTO != null && currentUserId.toUserBaseKey().equals(displayablePortfolioDTO.userBaseDTO.getBaseKey());
            if (!isCurrentUser)
            {
                subTitle = PortfolioDTOUtil.getLongSubTitle(
                        context,
                        displayablePortfolioDTO.portfolioDTO,
                        UserBaseDTOUtil.getLongDisplayName(context, displayablePortfolioDTO.userBaseDTO));
            }
            else
            {
                subTitle = PortfolioDTOUtil.getLongSubTitle(
                        context,
                        displayablePortfolioDTO.portfolioDTO);
            }
        }
        return subTitle;
    }
}
