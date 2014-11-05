package com.tradehero.th.api.billing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.samsung.THSamsungPurchase;
import android.support.annotation.NonNull;

public class SamsungPurchaseInProcessDTO extends SamsungPurchaseReportDTO
{
    public static final String GROUP_ID_JSON_KEY = "samsung_group_id";
    public static final String USER_TO_FOLLOW_JSON_KEY = "samsung_user_to_follow";
    public static final String APPLICABLE_PORTFOLIO_JSON_KEY = "samsung_applicable_portfolio";
    public static final String PURCHASE_VO_STRING_JSON_KEY = "samsung_purchase_vo";

    @JsonProperty(GROUP_ID_JSON_KEY)
    public String groupId;
    @JsonProperty(USER_TO_FOLLOW_JSON_KEY)
    public UserBaseKey userToFollow;
    @JsonProperty(APPLICABLE_PORTFOLIO_JSON_KEY)
    @NonNull public OwnedPortfolioId applicablePortfolioId;
    @JsonProperty(PURCHASE_VO_STRING_JSON_KEY)
    public String purchaseVoJsonString;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    SamsungPurchaseInProcessDTO()
    {
        // Necessary for Json deserialisation
        super();
    }

    public SamsungPurchaseInProcessDTO(@NonNull THSamsungPurchase samsungPurchase)
    {
        super(samsungPurchase);
        this.groupId = samsungPurchase.getGroupId();
        this.productCode = samsungPurchase.getItemId();
        this.userToFollow = samsungPurchase.getUserToFollow();
        this.applicablePortfolioId = samsungPurchase.getApplicableOwnedPortfolioId();
        this.purchaseVoJsonString = samsungPurchase.getJsonString();
    }
    //</editor-fold>
}
