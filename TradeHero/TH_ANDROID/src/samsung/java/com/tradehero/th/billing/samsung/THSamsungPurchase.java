package com.ayondo.academy.billing.samsung;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.samsung.android.sdk.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.samsung.SamsungPurchase;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.ayondo.academy.api.billing.SamsungPurchaseReportDTO;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.billing.THProductPurchase;

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
    public THSamsungPurchase(String _jsonString, @NonNull OwnedPortfolioId applicablePortfolioId)
    {
        super(_jsonString);
        this.applicablePortfolioId = applicablePortfolioId;
    }

    public THSamsungPurchase(PurchaseVo toCopyFrom, @NonNull OwnedPortfolioId applicablePortfolioId)
    {
        super(toCopyFrom);
        this.applicablePortfolioId = applicablePortfolioId;
    }
    //</editor-fold>

    @Override @NonNull public SamsungSKU getProductIdentifier()
    {
        return new SamsungSKU(getItemId());
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
}
