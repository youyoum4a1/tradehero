package com.tradehero.th.api.portfolio;

public class AssetClassDTO
{
    public AssetClass assetClass;

    public AssetClassDTO(AssetClass assetClass)
    {
        this.assetClass = assetClass;
    }

    @Override public String toString()
    {
        switch (assetClass)
        {
            case STOCKS:
                return "Stocks";
            case FX:
                return "FX";
            case CFD:
                return "CFD";
        }
        return "N/A";
    }
}
