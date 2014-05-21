package com.tradehero.th.api.security;

abstract public class BaseSecurityCompactDTOTest
{
    public boolean haveSameFields(SecurityCompactDTO left, SecurityCompactDTO right)
    {
        boolean have = left.id == null ? right.id == null : left.id.equals(right.id);
        have &= left.symbol == null ? right.symbol == null : left.symbol.equals(right.symbol);
        have &= left.securityType == right.securityType;
        have &= left.name == null ? right.name == null : left.name.equals(right.name);
        have &= left.exchange == null ? right.exchange == null : left.exchange.equals(right.exchange);
        have &= left.yahooSymbol == null ? right.yahooSymbol == null : left.yahooSymbol.equals(right.yahooSymbol);
        have &= left.currencyDisplay == null ? right.currencyDisplay == null : left.currencyDisplay.equals(right.currencyDisplay);
        have &= left.currencyISO == null ? right.currencyISO == null : left.currencyISO.equals(right.currencyISO);
        have &= left.marketCap == null ? right.marketCap == null : left.marketCap.equals(right.marketCap);
        have &= left.lastPrice == null ? right.lastPrice == null : left.lastPrice.equals(right.lastPrice);
        have &= left.imageBlobUrl == null ? right.imageBlobUrl == null : left.imageBlobUrl.equals(right.imageBlobUrl);
        have &= left.lastPriceDateAndTimeUtc == null ? right.lastPriceDateAndTimeUtc == null : left.lastPriceDateAndTimeUtc.equals(right.lastPriceDateAndTimeUtc);
        have &= left.toUSDRate == null ? right.toUSDRate == null : left.toUSDRate.equals(right.toUSDRate);
        have &= left.toUSDRateDate == null ? right.toUSDRateDate == null : left.toUSDRateDate.equals(right.toUSDRateDate);
        have &= left.active == right.active;
        have &= left.askPrice == null ? right.askPrice == null : left.askPrice.equals(right.askPrice);
        have &= left.bidPrice == null ? right.bidPrice == null : left.bidPrice.equals(right.bidPrice);
        have &= left.volume == null ? right.volume == null : left.volume.equals(right.volume);
        have &= left.averageDailyVolume == null ? right.averageDailyVolume == null : left.averageDailyVolume.equals(right.averageDailyVolume);
        have &= left.previousClose == null ? right.previousClose == null : left.previousClose.equals(right.previousClose);
        have &= left.open == null ? right.open == null : left.open.equals(right.open);
        have &= left.high == null ? right.high == null : left.high.equals(right.high);
        have &= left.low == null ? right.low == null : left.low.equals(right.low);
        have &= left.pe == null ? right.pe == null : left.pe.equals(right.pe);
        have &= left.eps == null ? right.eps == null : left.eps.equals(right.eps);
        have &= left.marketOpen == null ? right.marketOpen == null : left.marketOpen.equals(right.marketOpen);
        have &= left.pc50DMA == null ? right.pc50DMA == null : left.pc50DMA.equals(right.pc50DMA);
        have &= left.pc200DMA == null ? right.pc200DMA == null : left.pc200DMA.equals(right.pc200DMA);
        have &= left.exchangeTimezoneMsftName == null ? right.exchangeTimezoneMsftName == null : left.exchangeTimezoneMsftName.equals(right.exchangeTimezoneMsftName);
        have &= left.exchangeOpeningTimeLocal == null ? right.exchangeOpeningTimeLocal == null : left.exchangeOpeningTimeLocal.equals(right.exchangeOpeningTimeLocal);
        have &= left.exchangeClosingTimeLocal == null ? right.exchangeClosingTimeLocal == null : left.exchangeClosingTimeLocal.equals(right.exchangeClosingTimeLocal);
        have &= left.secTypeDesc == null ? right.secTypeDesc == null : left.secTypeDesc.equals(right.secTypeDesc);
        return have;
    }
}
