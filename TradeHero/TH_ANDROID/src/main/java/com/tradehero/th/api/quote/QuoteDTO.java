package com.tradehero.th.api.quote;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import java.util.Date;

public class QuoteDTO
{
    public int securityId;

    public Date asOfUtc;
    public Double bid;
    public Double ask;

    public String currencyISO;
    public String currencyDisplay;

    public boolean fromCache;
    public int quoteType;

    public Double toUSDRate;
    public Date toUSDRateDate;

    public String timeStamp;

    // This part is used for the signature container that came back
    public String rawResponse;

    @JsonIgnore public Double getPrice(boolean isBuy)
    {
        return isBuy ? ask : bid;
    }

    @JsonIgnore public Double getBidUSD()
    {
        if (bid == null || toUSDRate == null)
        {
            return null;
        }
        return bid * toUSDRate;
    }

    @JsonIgnore public Double getAskUSD()
    {
        if (ask == null || toUSDRate == null)
        {
            return null;
        }
        return ask * toUSDRate;
    }

    @JsonIgnore public Double getPriceUSD(boolean isBuy)
    {
        return isBuy ? getAskUSD() : getBidUSD();
    }

    /**
     *
     * @param refCcyToUsdRate Pass 1 if refCcy is USD
     * @return
     */
    @JsonIgnore public Double getBidRefCcy(Double refCcyToUsdRate)
    {
        Double bidUSD = getBidUSD();
        if (bidUSD == null || refCcyToUsdRate == null || refCcyToUsdRate.equals(0d))
        {
            return null;
        }
        return bidUSD / refCcyToUsdRate;
    }

    /**
     *
     * @param refCcyToUsdRate Pass 1 if refCcy is USD
     * @return
     */
    @JsonIgnore public Double getAskRefCcy(Double refCcyToUsdRate)
    {
        Double askUSD = getAskUSD();
        if (askUSD == null || refCcyToUsdRate == null || refCcyToUsdRate.equals(0d))
        {
            return null;
        }
        return askUSD / refCcyToUsdRate;
    }

    @JsonIgnore public Double getPriceRefCcy(Double refCcyToUsdRate, boolean isBuy)
    {
        return isBuy ? getAskRefCcy(refCcyToUsdRate) : getBidRefCcy(refCcyToUsdRate);
    }

    @JsonIgnore public Double getBidRefCcy(PortfolioCompactDTO portfolioCompactDTO)
    {
        if (portfolioCompactDTO == null)
        {
            return null;
        }
        return getBidRefCcy(portfolioCompactDTO.getProperRefCcyToUsdRate());
    }

    @JsonIgnore public Double getAskRefCcy(PortfolioCompactDTO portfolioCompactDTO)
    {
        if (portfolioCompactDTO == null)
        {
            return null;
        }
        return getAskRefCcy(portfolioCompactDTO.getProperRefCcyToUsdRate());
    }

    @JsonIgnore public Double getPriceRefCcy(PortfolioCompactDTO portfolioCompactDTO, boolean isBuy)
    {
        return isBuy ? getAskRefCcy(portfolioCompactDTO) : getBidRefCcy(portfolioCompactDTO);
    }
}
