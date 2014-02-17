package com.tradehero.th.api.portfolio;

/**
 * Created by xavier on 2/17/14.
 */
public class BaseOwnedPorfolioIdTest
{
    protected OwnedPortfolioId get1n2()
    {
        return new OwnedPortfolioId(1, 2);
    }

    protected OwnedPortfolioId get1n3()
    {
        return new OwnedPortfolioId(1, 3);
    }

    protected OwnedPortfolioId get3n2()
    {
        return new OwnedPortfolioId(3, 2);
    }
}
