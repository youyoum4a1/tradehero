package com.ayondo.academy.api.portfolio;

public class DummyFxDisplayablePortfolioDTO extends DisplayablePortfolioDTO
{
    public DummyFxDisplayablePortfolioDTO()
    {
        super();
        portfolioDTO = new PortfolioDTO();
        portfolioDTO.title = "My FX";
        portfolioDTO.assetClass = AssetClass.FX;
    }
}
