package com.tradehero.th.utils;

import com.tradehero.th.R;
import com.tradehero.th.base.Application;

public class SecurityUtils
{
    public static final String DEFAULT_VIRTUAL_CASH_CURRENCY_ISO = "USD";
    public static final String DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY = "US$";

    public static final double DEFAULT_TRANSACTION_COST = 10;
    public static final String DEFAULT_TRANSACTION_CURRENCY_ISO = "USD";
    public static final String DEFAULT_TRANSACTION_CURRENCY_DISPLAY = "US$";

  public static String getDefaultCurrency() {
    return Application.context().getString(R.string.currency);
  }
}
