package com.tradehero.th.billing.samsung;

import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.samsung.SamsungPurchase;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.api.billing.SamsungPurchaseInProcessDTO;
import com.tradehero.th.api.billing.SamsungPurchaseReportDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.THProductPurchase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class THSamsungPurchase
    extends SamsungPurchase<
        SamsungSKU,
        THSamsungOrderId>
    implements THProductPurchase<
        SamsungSKU,
        THSamsungOrderId>
{
    @Nullable private UserBaseKey userToFollow;
    @NonNull private OwnedPortfolioId applicablePortfolioId;

    //<editor-fold desc="Constructors">
    public THSamsungPurchase(String groupId, String _jsonString, @NonNull OwnedPortfolioId applicablePortfolioId)
    {
        super(groupId, _jsonString);
        this.applicablePortfolioId = applicablePortfolioId;
    }

    public THSamsungPurchase(String groupId, PurchaseVo toCopyFrom, @NonNull OwnedPortfolioId applicablePortfolioId)
    {
        super(groupId, toCopyFrom);
        this.applicablePortfolioId = applicablePortfolioId;
    }
    //</editor-fold>

    @Override @NonNull public SamsungSKU getProductIdentifier()
    {
        return new SamsungSKU(getGroupId(), getItemId());
    }

    @Override @NonNull public THSamsungOrderId getOrderId()
    {
        return new THSamsungOrderId(getPurchaseId());
    }

    @Override @Nullable public UserBaseKey getUserToFollow()
    {
        return userToFollow;
    }

    @Override public void setUserToFollow(@Nullable UserBaseKey userToFollow)
    {
        this.userToFollow = userToFollow;
    }

    @Override @NonNull public OwnedPortfolioId getApplicableOwnedPortfolioId()
    {
        return applicablePortfolioId;
    }

    public void setApplicablePortfolioId(@NonNull OwnedPortfolioId applicablePortfolioId)
    {
        this.applicablePortfolioId = applicablePortfolioId;
    }

    @Override @NonNull public SamsungPurchaseReportDTO getPurchaseReportDTO()
    {
        return new SamsungPurchaseReportDTO(this);
    }

    @NonNull public SamsungPurchaseInProcessDTO getPurchaseToSaveDTO()
    {
        return new SamsungPurchaseInProcessDTO(this);
    }

    public void populate(@NonNull SamsungPurchaseInProcessDTO purchaseInProcessDTO)
    {
        if (!getPaymentId().equals(purchaseInProcessDTO.paymentId))
        {
            throw new IllegalArgumentException(String.format("Non-matching paymentId %s - %s", getPaymentId(), purchaseInProcessDTO.paymentId));
        }
        setPurchaseId(purchaseInProcessDTO.purchaseId);
        setApplicablePortfolioId(purchaseInProcessDTO.applicablePortfolioId);
        setUserToFollow(purchaseInProcessDTO.userToFollow);
        setItemId(purchaseInProcessDTO.productCode);
    }
}
