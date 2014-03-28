package com.tradehero.th.api.users;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.api.alert.UserAlertPlanDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.billing.googleplay.THIABSecurityAlertKnowledge;
import java.util.ArrayList;
import javax.inject.Inject;

/**
 * Created by xavier on 12/13/13.
 */
public class UserProfileDTOUtil
{
    public static final String TAG = UserProfileDTOUtil.class.getSimpleName();

    @Inject protected THIABSecurityAlertKnowledge THIABSecurityAlertKnowledge;
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

                    serverEquivalent = THIABSecurityAlertKnowledge.getServerEquivalentSKU(localSKU);
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
