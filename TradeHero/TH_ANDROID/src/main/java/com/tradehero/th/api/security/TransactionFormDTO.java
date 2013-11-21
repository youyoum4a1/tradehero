package com.tradehero.th.api.security;

import com.tradehero.th.api.timeline.PublishableFormDTO;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 10/8/13 Time: 10:09 AM To change this template use File | Settings | File Templates. */
public class TransactionFormDTO extends PublishableFormDTO
{
    public static final String POST_KEY_SIGNED_QUOTE_DTO = "signedQuoteDto";
    public static final String POST_KEY_QUANTITY = "quantity";
    public static final String POST_KEY_PORTFOLIO = "portfolio";

    public String signedQuoteDto;
    public int quantity;
    public int portfolio;

    public TransactionFormDTO()
    {
    }

    public TransactionFormDTO(Boolean publishToFb, Boolean publishToTw, Boolean publishToLi, String geo_alt, String geo_lat, String geo_long,
            boolean aPublic, String tradeComment, String signedQuoteDto, int quantity, int portfolio)
    {
        super(publishToFb, publishToTw, publishToLi, geo_alt, geo_lat, geo_long, aPublic, tradeComment);
        this.portfolio = portfolio;
        this.quantity = quantity;
        this.signedQuoteDto = signedQuoteDto;
    }

    @Override public Map<String, String> toStringMap()
    {
        Map<String, String> map = super.toStringMap();
        if (signedQuoteDto != null)
        {
            map.put(POST_KEY_SIGNED_QUOTE_DTO, signedQuoteDto);
        }
        map.put(POST_KEY_QUANTITY, String.format("%d", quantity));
        map.put(POST_KEY_PORTFOLIO, String.format("%d", portfolio));
        return map;
    }

    @Override public String toString()
    {
        return "TransactionFormDTO{" +
                "portfolio=" + portfolio +
                ", signedQuoteDto='" + signedQuoteDto + '\'' +
                ", quantity=" + quantity +
                ", geo_alt='" + geo_alt + '\'' +
                ", publishToFb=" + publishToFb +
                ", publishToTw=" + publishToTw +
                ", publishToLi=" + publishToLi +
                ", geo_lat='" + geo_lat + '\'' +
                ", geo_long='" + geo_long + '\'' +
                ", isPublic=" + isPublic +
                ", tradeComment='" + tradeComment + '\'' +
                '}';
    }
}
