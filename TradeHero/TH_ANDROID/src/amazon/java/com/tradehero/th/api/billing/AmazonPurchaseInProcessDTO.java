package com.tradehero.th.api.billing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.amazon.THBaseAmazonPurchase;
import org.jetbrains.annotations.NotNull;

public class AmazonPurchaseInProcessDTO extends AmazonPurchaseReportDTO
{
    public static final String USER_TO_FOLLOW_JSON_KEY = "amazon_user_to_follow";
    public static final String APPLICABLE_PORTFOLIO_JSON_KEY = "amazon_applicable_portfolio";

    @JsonProperty(USER_TO_FOLLOW_JSON_KEY)
    public UserBaseKey userToFollow;
    @JsonProperty(APPLICABLE_PORTFOLIO_JSON_KEY)
    @NotNull public OwnedPortfolioId applicablePortfolioId;

    //<editor-fold desc="Constructors">
    protected AmazonPurchaseInProcessDTO()
    {
        super();
    }

    public AmazonPurchaseInProcessDTO(@NotNull THBaseAmazonPurchase amazonPurchase)
    {
        super(amazonPurchase);
        this.userToFollow = amazonPurchase.getUserToFollow();
        this.applicablePortfolioId = amazonPurchase.getApplicableOwnedPortfolioId();
    }
    //</editor-fold>
}
