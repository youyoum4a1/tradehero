package com.tradehero.th.billing;

public interface THPurchaseActionInteractor
{
    void onDestroy();

    int showProductsList(ProductIdentifierDomain domain);

    int buyVirtualDollar();
    int buyFollowCredits();
    int buyStockAlertSubscription();
    int resetPortfolio();

    void premiumFollowUser();
}
