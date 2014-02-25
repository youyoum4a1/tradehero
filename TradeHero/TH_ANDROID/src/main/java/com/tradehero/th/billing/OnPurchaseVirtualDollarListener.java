package com.tradehero.th.billing;

import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserProfileDTO;

/**
 * Created by xavier on 2/24/14.
 */
public interface OnPurchaseVirtualDollarListener
{
    void onPurchasedVirtualDollar(OwnedPortfolioId ownedPortfolioId, UserProfileDTO userProfileDTO);
    void onPurchasedVirtualDollarFailed(OwnedPortfolioId ownedPortfolioId, BillingException billingException);

}
