package com.tradehero.th.billing.samsung;

import android.content.Context;
import com.tradehero.common.billing.samsung.BaseSamsungBillingAvailableTester;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/**
 * Created by xavier on 3/27/14.
 */
public class THBaseSamsungBillingAvailableTester
    extends BaseSamsungBillingAvailableTester<SamsungException>
    implements THSamsungBillingAvailableTester
{
    @Inject protected THSamsungExceptionFactory samsungExceptionFactory;

    public THBaseSamsungBillingAvailableTester(Context context, int mode)
    {
        super(context, mode);
        DaggerUtils.inject(this);
    }

    @Override protected SamsungException createSamsungException(int result)
    {
        return samsungExceptionFactory.create(result);
    }
}
