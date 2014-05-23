package com.tradehero.th.api.users;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.api.alert.UserAlertPlanDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.billing.googleplay.SecurityAlertKnowledge;
import java.util.ArrayList;
import javax.inject.Inject;

public class UserProfileDTOUtil
{
    public final static int IS_NOT_FOLLOWER_WANT_MSG = -1;
    public final static int IS_NOT_FOLLOWER = 0;
    public final static int IS_FREE_FOLLOWER = 1;
    public final static int IS_PREMIUM_FOLLOWER = 2;

    @Inject protected SecurityAlertKnowledge securityAlertKnowledge;
    @Inject protected PortfolioCompactDTOUtil portfolioCompactDTOUtil;

    @Inject public UserProfileDTOUtil()
    {
    }

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
