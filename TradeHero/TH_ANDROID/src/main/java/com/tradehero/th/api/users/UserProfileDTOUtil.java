package com.tradehero.th.api.users;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.th.api.alert.UserAlertPlanDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.billing.SecurityAlertKnowledge;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import javax.inject.Inject;

public class UserProfileDTOUtil extends UserBaseDTOUtil
{
    public final static int IS_NOT_FOLLOWER_WANT_MSG = -1;
    public final static int IS_NOT_FOLLOWER = 0;
    public final static int IS_FREE_FOLLOWER = 1;
    public final static int IS_PREMIUM_FOLLOWER = 2;

    @Inject protected SecurityAlertKnowledge securityAlertKnowledge;
    @Inject protected PortfolioCompactDTOUtil portfolioCompactDTOUtil;

    //<editor-fold desc="Constructors">
    @Inject public UserProfileDTOUtil(
            @NotNull SecurityAlertKnowledge securityAlertKnowledge,
            @NotNull PortfolioCompactDTOUtil portfolioCompactDTOUtil)
    {
        super();
        this.securityAlertKnowledge = securityAlertKnowledge;
        this.portfolioCompactDTOUtil = portfolioCompactDTOUtil;
    }
    //</editor-fold>

    //<editor-fold desc="Max Purchasable Shares">
    public Integer getMaxPurchasableShares(UserProfileDTO userProfileDTO, QuoteDTO quoteDTO)
    {
        return getMaxPurchasableShares(userProfileDTO, quoteDTO, true);
    }

    public Integer getMaxPurchasableShares(
            UserProfileDTO userProfileDTO,
            QuoteDTO quoteDTO,
            boolean includeTransactionCost)
    {
        if (userProfileDTO == null || userProfileDTO.portfolio == null)
        {
            return null;
        }
        return portfolioCompactDTOUtil.getMaxPurchasableShares(userProfileDTO.portfolio, quoteDTO, includeTransactionCost);
    }
    //</editor-fold>

    @NotNull public ArrayList<ProductIdentifier> getSubscribedAlerts(
            @NotNull UserProfileDTO userProfileDTO)
    {
        ArrayList<ProductIdentifier> subscribedAlerts = new ArrayList<>();
        if (userProfileDTO.userAlertPlans != null)
        {
            ProductIdentifier localSKU;
            ProductIdentifier serverEquivalent;
            for (UserAlertPlanDTO userAlertPlanDTO : userProfileDTO.userAlertPlans)
            {
                if (userAlertPlanDTO != null &&
                        userAlertPlanDTO.alertPlan != null &&
                        userAlertPlanDTO.alertPlan.productIdentifier != null)
                {
                    localSKU = securityAlertKnowledge.createFrom(userAlertPlanDTO.alertPlan);
                    subscribedAlerts.add(localSKU);

                    serverEquivalent = securityAlertKnowledge.getServerEquivalentSKU(localSKU);
                    if (serverEquivalent != null)
                    {
                        subscribedAlerts.add(serverEquivalent);
                    }
                }
            }
        }
        return subscribedAlerts;
    }
}
