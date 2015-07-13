package com.tradehero.chinabuild.fragment.security;

import com.tradehero.common.persistence.DTO;

/**
 * Created by palmer on 15/7/10.
 */
public class SecurityOptPositionDTO implements DTO{

    public int id;
    public int userId;
    public int securityId;
    public int portfolioId;
    public String name;
    public String exchange;
    public String symbol;
    public int shares = 0;
    public int sellableShares = 0;
    public double averagePriceRefCcy;
    public String currencyDisplay;
    public double fxRate;
    public double realizedPLRefCcy;
    public double unrealizedPLRefCcy;
    public double marketValueRefCcy;
    public double sumInvestedAmountRefCcy;
    public double roi;
}
