package com.tradehero.th.billing.samsung;

import com.sec.android.iap.lib.vo.InboxVo;
import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.samsung.SamsungPurchase;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.api.billing.SamsungPurchaseInProcessDTO;
import com.tradehero.th.api.billing.SamsungPurchaseReportDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.THProductPurchase;

/**
 * Created by xavier on 3/27/14.
 */
public class THSamsungPurchase
    extends SamsungPurchase<
        SamsungSKU,
        THSamsungOrderId>
    implements THProductPurchase<
        SamsungSKU,
        THSamsungOrderId>
{
    private UserBaseKey userToFollow;
    private OwnedPortfolioId applicablePortfolioId;

    //<editor-fold desc="Constructors">
    public THSamsungPurchase(String groupId, String _jsonString, OwnedPortfolioId applicablePortfolioId)
    {
        super(groupId, _jsonString);
        this.applicablePortfolioId = applicablePortfolioId;
    }

    public THSamsungPurchase(String groupId, PurchaseVo toCopyFrom, OwnedPortfolioId applicablePortfolioId)
    {
        super(groupId, toCopyFrom);
        this.applicablePortfolioId = applicablePortfolioId;
    }

    public THSamsungPurchase(String groupId, InboxVo toCopyFrom, OwnedPortfolioId applicablePortfolioId)
    {
        super(groupId, toCopyFrom);
        this.applicablePortfolioId = applicablePortfolioId;
    }
    //</editor-fold>

    @Override public SamsungSKU getProductIdentifier()
    {
        return new SamsungSKU(getGroupId(), getItemId());
    }

    @Override public THSamsungOrderId getOrderId()
    {
        return new THSamsungOrderId(getPurchaseId());
    }

    @Override public UserBaseKey getUserToFollow()
    {
        return userToFollow;
    }

    @Override public void setUserToFollow(UserBaseKey userToFollow)
    {
        this.userToFollow = userToFollow;
    }

    @Override public OwnedPortfolioId getApplicableOwnedPortfolioId()
    {
        return applicablePortfolioId;
    }

    public void setApplicablePortfolioId(OwnedPortfolioId applicablePortfolioId)
    {
        this.applicablePortfolioId = applicablePortfolioId;
    }

    public SamsungPurchaseReportDTO getPurchaseDTO()
    {
        return new SamsungPurchaseReportDTO(this);
    }

    public SamsungPurchaseInProcessDTO getPurchaseToSaveDTO()
    {
        return new SamsungPurchaseInProcessDTO(this);
    }

    public void populate(SamsungPurchaseInProcessDTO purchaseInProcessDTO)
    {
        if (!getPaymentId().equals(purchaseInProcessDTO.paymentId))
        {
            throw new IllegalArgumentException(String.format("Non-matching paymentId %s - %s", getPaymentId(), purchaseInProcessDTO.paymentId));
        }
        setPurchaseId(purchaseInProcessDTO.purchaseId);
        setApplicablePortfolioId(purchaseInProcessDTO.applicablePortfolioId);
        setUserToFollow(purchaseInProcessDTO.userToFollow);
        setProductCode(purchaseInProcessDTO.productCode);
    }
}
