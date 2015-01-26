package com.tradehero.th.billing.googleplay.report;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.report.THPurchaseReporterHolderRx;

public interface THIABPurchaseReporterHolderRx extends
        THPurchaseReporterHolderRx<
                IABSKU,
                THIABProductDetail,
                THIABOrderId,
                THIABPurchase>
{
}
