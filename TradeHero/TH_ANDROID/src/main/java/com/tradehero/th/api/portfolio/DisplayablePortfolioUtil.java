package com.tradehero.th.api.portfolio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
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

    @NonNull public String getLongTitle(@NonNull Context context, @Nullable DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        String title = null;
        if (displayablePortfolioDTO != null && displayablePortfolioDTO.portfolioDTO != null)
        {
            title = portfolioDTOUtil.get().getLongTitle(context,
                    displayablePortfolioDTO.portfolioDTO);
        }
        if (title != null)
        {
            return title;
        }
        return context.getString(R.string.portfolio_title_unnamed);
    }

    public int getLongTitleTextColor(@NonNull Context context, @Nullable DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        int colorRes;
        if (displayablePortfolioDTO instanceof DummyFxDisplayablePortfolioDTO)
        {
            colorRes = R.color.gray_1;
        }
        else
        {
            colorRes = R.color.black;
        }
        return context.getResources().getColor(colorRes);
    }

    public String getLongSubTitle(@NonNull Context context, @Nullable DisplayablePortfolioDTO displayablePortfolioDTO)
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
