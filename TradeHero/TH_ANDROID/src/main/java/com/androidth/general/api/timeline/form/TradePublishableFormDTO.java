package com.androidth.general.api.timeline.form;

import android.support.annotation.NonNull;
import java.util.Map;

public class TradePublishableFormDTO extends PublishableFormDTO
{
    public String tradeComment;

    //<editor-fold desc="Constructors">
    public TradePublishableFormDTO()
    {
        super();
    }

    public TradePublishableFormDTO(Boolean publishToFb, Boolean publishToTw, Boolean publishToLi, Boolean publishToWb, String geo_alt, String geo_lat, String geo_long,
            boolean aPublic,
            String tradeComment)
    {
        super(publishToFb, publishToTw, publishToLi,publishToWb, geo_alt, geo_lat, geo_long, aPublic);
        this.tradeComment = tradeComment;
    }
    //</editor-fold>

    @NonNull @Override public Map<String, String> toStringMap()
    {
        Map<String, String> map = super.toStringMap();
        if (tradeComment != null)
        {
            map.put(POST_KEY_TRADE_COMMENT, tradeComment);
        }
        return map;
    }
}
