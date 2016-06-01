package com.ayondo.academy.api.billing;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.billing.amazon.THBaseAmazonPurchase;

public class AmazonPurchaseInProcessDTO extends AmazonPurchaseReportDTO
{
    public static final String USER_TO_FOLLOW_JSON_KEY = "amazon_user_to_follow";
    public static final String APPLICABLE_PORTFOLIO_JSON_KEY = "amazon_applicable_portfolio";

    @JsonProperty(USER_TO_FOLLOW_JSON_KEY)
    public UserBaseKey userToFollow;
    @JsonProperty(APPLICABLE_PORTFOLIO_JSON_KEY)
    @NonNull public OwnedPortfolioId applicablePortfolioId;

    //<editor-fold desc="Constructors">
    protected AmazonPurchaseInProcessDTO()
    {
        // Needed for deserialisation
        super();
    }

    public AmazonPurchaseInProcessDTO(@NonNull THBaseAmazonPurchase amazonPurchase)
    {
        super(amazonPurchase);
        this.userToFollow = amazonPurchase.getUserToFollow();
        this.applicablePortfolioId = amazonPurchase.getApplicableOwnedPortfolioId();
    }
    //</editor-fold>
}
