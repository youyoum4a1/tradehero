package com.tradehero.th.api.users;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.th.api.alert.UserAlertPlanDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.billing.SecurityAlertKnowledge;
import com.tradehero.th.persistence.prefs.FirstShowOnBoardDialog;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserProfileDTOUtil extends UserBaseDTOUtil
{
    public final static int IS_NOT_FOLLOWER_WANT_MSG = -1;
    public final static int IS_NOT_FOLLOWER = 0;
    public final static int IS_FREE_FOLLOWER = 1;
    public final static int IS_PREMIUM_FOLLOWER = 2;

    @NotNull protected final SecurityAlertKnowledge securityAlertKnowledge;
    @NotNull protected final PortfolioCompactDTOUtil portfolioCompactDTOUtil;
    @NotNull protected final TimingIntervalPreference firstShowOnBoardDialogPreference;

    //<editor-fold desc="Constructors">
    @Inject public UserProfileDTOUtil(
            @NotNull SecurityAlertKnowledge securityAlertKnowledge,
            @NotNull PortfolioCompactDTOUtil portfolioCompactDTOUtil,
            @NotNull @FirstShowOnBoardDialog TimingIntervalPreference firstShowOnBoardDialogPreference)
    {
        super();
        this.securityAlertKnowledge = securityAlertKnowledge;
        this.portfolioCompactDTOUtil = portfolioCompactDTOUtil;
        this.firstShowOnBoardDialogPreference = firstShowOnBoardDialogPreference;
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

    public boolean shouldShowOnBoard(@Nullable UserProfileDTO currentUserProfile)
    {
        if (firstShowOnBoardDialogPreference.isItTime())
        {
            if (currentUserProfile != null)
            {
                List<Integer> userGenHeroIds = currentUserProfile.getUserGeneratedHeroIds();
                if (userGenHeroIds != null && userGenHeroIds.size() > 0)
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean checkLinkedStatus(@NotNull UserProfileCompactDTO userProfileCompactDTO, @NotNull SocialNetworkEnum socialNetwork)
    {
        switch (socialNetwork)
        {
            case FB:
                return userProfileCompactDTO.fbLinked;
            case LN:
                return userProfileCompactDTO.liLinked;
            case QQ:
                return userProfileCompactDTO.qqLinked;
            case TH:
                return userProfileCompactDTO.thLinked;
            case TW:
                return userProfileCompactDTO.twLinked;
            case WB:
                return userProfileCompactDTO.wbLinked;
            default:
                return false;
        }
    }
}
