package com.androidth.general.api.users;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.api.discussion.MessageType;
import com.androidth.general.api.social.SocialNetworkEnum;
//import com.androidth.general.billing.BillingSecurityAlertKnowledge;
import com.androidth.general.persistence.prefs.FirstShowOnBoardDialog;
import com.androidth.general.persistence.timing.TimingIntervalPreference;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class UserProfileDTOUtil extends UserBaseDTOUtil
{
    public final static int IS_NOT_FOLLOWER_WANT_MSG = -1;
    public final static int IS_NOT_FOLLOWER = 0;
    public final static int IS_FREE_FOLLOWER = 1;

    @NonNull protected final TimingIntervalPreference firstShowOnBoardDialogPreference;

    //<editor-fold desc="Constructors">
    @Inject public UserProfileDTOUtil(
            @NonNull @FirstShowOnBoardDialog TimingIntervalPreference firstShowOnBoardDialogPreference)
    {
        super();
        this.firstShowOnBoardDialogPreference = firstShowOnBoardDialogPreference;
    }
    //</editor-fold>

    @NonNull public static ArrayList<ProductIdentifier> getSubscribedAlerts(@NonNull UserProfileDTO userProfileDTO)
    {
        ArrayList<ProductIdentifier> subscribedAlerts = new ArrayList<>();
//        if (userProfileDTO.userAlertPlans != null)
//        {
//            ProductIdentifier localSKU;
//            ProductIdentifier serverEquivalent;
//            for (UserAlertPlanDTO userAlertPlanDTO : userProfileDTO.userAlertPlans)
//            {
//                if (userAlertPlanDTO != null &&
//                        userAlertPlanDTO.alertPlan != null &&
//                        userAlertPlanDTO.alertPlan.productIdentifier != null)
//                {
//                    localSKU = BillingSecurityAlertKnowledge.createFrom(userAlertPlanDTO.alertPlan);
//                    subscribedAlerts.add(localSKU);
//
//                    serverEquivalent = BillingSecurityAlertKnowledge.getServerEquivalentSKU(localSKU);
//                    if (serverEquivalent != null)
//                    {
//                        subscribedAlerts.add(serverEquivalent);
//                    }
//                }
//            }
//        }
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

    public static boolean checkLinkedStatus(@NonNull UserProfileCompactDTO userProfileCompactDTO, @NonNull SocialNetworkEnum socialNetwork)
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

    public static int getFollowerCountByUserProfile(@NonNull MessageType messageType, @NonNull UserProfileDTO userProfileDTO)
    {
        switch (messageType)
        {
            case BROADCAST_FREE_FOLLOWERS:
                return userProfileDTO.freeFollowerCount;

            case BROADCAST_PAID_FOLLOWERS:
                return userProfileDTO.paidFollowerCount;

            case BROADCAST_ALL_FOLLOWERS:
                return userProfileDTO.allFollowerCount;

            default:
                throw new IllegalArgumentException("unknown messageType");
        }
    }
}
