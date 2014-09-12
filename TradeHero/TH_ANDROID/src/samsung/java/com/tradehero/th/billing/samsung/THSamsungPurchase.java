package com.tradehero.th.billing.samsung;

import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.samsung.SamsungPurchase;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.api.billing.SamsungPurchaseInProcessDTO;
import com.tradehero.th.api.billing.SamsungPurchaseReportDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.THProductPurchase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class THSamsungPurchase
    extends SamsungPurchase<
        SamsungSKU,
        THSamsungOrderId>
    implements THProductPurchase<
        SamsungSKU,
        THSamsungOrderId>
{
    @Nullable private UserBaseKey userToFollow;
    @NotNull private OwnedPortfolioId applicablePortfolioId;

    //<editor-fold desc="Constructors">
    public THSamsungPurchase(String groupId, String _jsonString, @NotNull OwnedPortfolioId applicablePortfolioId)
    {
        super(groupId, _jsonString);
        this.applicablePortfolioId = applicablePortfolioId;
    }

    public THSamsungPurchase(String groupId, PurchaseVo toCopyFrom, @NotNull OwnedPortfolioId applicablePortfolioId)
    {
        super(groupId, toCopyFrom);
        this.applicablePortfolioId = applicablePortfolioId;
    }
    //</editor-fold>

    @Override @NotNull public SamsungSKU getProductIdentifier()
    {
        return new SamsungSKU(getGroupId(), getItemId());
    }

    @Override @NotNull public THSamsungOrderId getOrderId()
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

    @Override @NotNull public OwnedPortfolioId getApplicableOwnedPortfolioId()
    {
        return applicablePortfolioId;
    }

    public void setApplicablePortfolioId(@NotNull OwnedPortfolioId applicablePortfolioId)
    {
        this.applicablePortfolioId = applicablePortfolioId;
    }

    @Override @NotNull public SamsungPurchaseReportDTO getPurchaseReportDTO()
    {
        return new SamsungPurchaseReportDTO(this);
    }

    @NotNull public SamsungPurchaseInProcessDTO getPurchaseToSaveDTO()
    {
        return new SamsungPurchaseInProcessDTO(this);
    }

    public void populate(@NotNull SamsungPurchaseInProcessDTO purchaseInProcessDTO)
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
