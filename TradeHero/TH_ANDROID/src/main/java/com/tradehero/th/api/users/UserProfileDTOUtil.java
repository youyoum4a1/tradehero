package com.tradehero.th.api.users;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.api.alert.UserAlertPlanDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.billing.googleplay.SecurityAlertKnowledge;
import java.util.ArrayList;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class UserProfileDTOUtil extends UserBaseDTOUtil
{
    public final static int IS_NOT_FOLLOWER_WANT_MSG = -1;
    public final static int IS_NOT_FOLLOWER = 0;
    public final static int IS_FREE_FOLLOWER = 1;
    public final static int IS_PREMIUM_FOLLOWER = 2;

    @NotNull protected SecurityAlertKnowledge securityAlertKnowledge;
    @NotNull protected PortfolioCompactDTOUtil portfolioCompactDTOUtil;

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

    public Integer getMaxPurchasableShares(UserProfileDTO userProfileDTO, QuoteDTO quoteDTO)
    {
        return getMaxPurchasableShares(userProfileDTO, quoteDTO, true);
    }

    public Integer getMaxPurchasableShares(UserProfileDTO userProfileDTO, QuoteDTO quoteDTO, boolean includeTransactionCost)
    {
        if (userProfileDTO == null || userProfileDTO.portfolio == null)
        {
            return null;
        }
        return portfolioCompactDTOUtil.getMaxPurchasableShares(userProfileDTO.portfolio, quoteDTO, includeTransactionCost);
    }

    public ArrayList<IABSKU> getSubscribedAlerts(UserProfileDTO userProfileDTO)
    {
        if (userProfileDTO == null)
        {
            return null;
        }

        ArrayList<IABSKU> subscribedAlerts = new ArrayList<>();
        if (userProfileDTO.userAlertPlans != null)
        {
            IABSKU localSKU;
            IABSKU serverEquivalent;
            for (UserAlertPlanDTO userAlertPlanDTO : userProfileDTO.userAlertPlans)
            {
                if (userAlertPlanDTO != null &&
                        userAlertPlanDTO.alertPlan != null &&
                        userAlertPlanDTO.alertPlan.productIdentifier != null)
                {
                    localSKU = new IABSKU(userAlertPlanDTO.alertPlan.productIdentifier);
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
