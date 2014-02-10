package com.tradehero.th.api.users;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.api.alert.UserAlertPlanDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.billing.googleplay.SecurityAlertKnowledge;
import com.tradehero.th.utils.SecurityUtils;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 12/13/13.
 */
@Singleton public class UserProfileDTOUtil
{
    public static final String TAG = UserProfileDTOUtil.class.getSimpleName();

    @Inject protected SecurityAlertKnowledge securityAlertKnowledge;

    @Inject public UserProfileDTOUtil()
    {
    }

    public Integer getMaxPurchasableShares(UserProfileDTO userProfileDTO, QuoteDTO quoteDTO)
    {
        return getMaxPurchasableShares(userProfileDTO, quoteDTO, true);
    }

    public Integer getMaxPurchasableShares(UserProfileDTO userProfileDTO, QuoteDTO quoteDTO, boolean includeTransactionCost)
    {
        if (quoteDTO == null || quoteDTO.ask == null || quoteDTO.ask == 0 || quoteDTO.toUSDRate == null || quoteDTO.toUSDRate == 0 ||
                userProfileDTO == null || userProfileDTO.portfolio == null)
        {
            return null;
        }
        return (int) Math.floor((userProfileDTO.portfolio.cashBalance - (includeTransactionCost ? SecurityUtils.DEFAULT_TRANSACTION_COST : 0)) / (quoteDTO.ask * quoteDTO.toUSDRate));
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
