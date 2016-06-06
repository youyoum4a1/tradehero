package com.androidth.general.billing.googleplay.report;

import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.billing.googleplay.THIABOrderId;
import com.androidth.general.billing.googleplay.THIABProductDetail;
import com.androidth.general.billing.googleplay.THIABPurchase;
import com.androidth.general.billing.report.THPurchaseReporterHolderRx;

public interface THIABPurchaseReporterHolderRx extends
        THPurchaseReporterHolderRx<
                IABSKU,
                THIABProductDetail,
                THIABOrderId,
                THIABPurchase>
{
}
