package com.tradehero.th.api.timeline;

import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 10/8/13 Time: 10:09 AM To change this template use File | Settings | File Templates. */
public class PublishableFormDTO
{
    public static final String POST_KEY_PUBLISH_TO_FACEBOOK = "publishToFb";
    public static final String POST_KEY_PUBLISH_TO_TWITTER = "publishToTw";
    public static final String POST_KEY_PUBLISH_TO_LINKEDIN = "publishToLi";
    public static final String POST_KEY_GEO_ALT = "geo_alt";
    public static final String POST_KEY_GEO_LAT = "geo_lat";
    public static final String POST_KEY_GEO_LONG = "geo_long";
    public static final String POST_KEY_IS_PUBLIC = "isPublic";
    public static final String POST_KEY_TRADE_COMMENT = "tradeComment";

    public Boolean publishToFb;
    public Boolean publishToTw;
    public Boolean publishToLi;

    public String geo_alt;
    public String geo_lat;
    public String geo_long;

    public boolean isPublic;

    public String tradeComment;

    public PublishableFormDTO()
    {
    }

    public PublishableFormDTO(Boolean publishToFb, Boolean publishToTw, Boolean publishToLi, String geo_alt, String geo_lat, String geo_long,
            boolean aPublic, String tradeComment)
    {
        this.publishToFb = publishToFb;
        this.publishToTw = publishToTw;
        this.publishToLi = publishToLi;
        this.geo_alt = geo_alt;
        this.geo_lat = geo_lat;
        this.geo_long = geo_long;
        isPublic = aPublic;
        this.tradeComment = tradeComment;
    }

    public Map<String, String> toStringMap()
    {
        Map<String, String> map = new HashMap<>();
        if (publishToFb != null)
        {
            map.put(POST_KEY_PUBLISH_TO_FACEBOOK, publishToFb ? "1" : "0");
        }
        if (publishToTw != null)
        {
            map.put(POST_KEY_PUBLISH_TO_TWITTER, publishToTw ? "1" : "0");
        }
        if (publishToLi != null)
        {
            map.put(POST_KEY_PUBLISH_TO_LINKEDIN, publishToLi ? "1" : "0");
        }
        if (geo_alt != null)
        {
            map.put(POST_KEY_GEO_ALT, geo_alt);
        }
        if (geo_lat != null)
        {
            map.put(POST_KEY_GEO_LAT, geo_lat);
        }
        if (geo_long != null)
        {
            map.put(POST_KEY_GEO_LONG, geo_long);
        }
        map.put(POST_KEY_IS_PUBLIC, isPublic ? "1" : "0");
        if (tradeComment != null)
        {
            map.put(POST_KEY_TRADE_COMMENT, tradeComment);
        }
        return map;
    }

    @Override public String toString()
    {
        return "PublishableFormDTO{" +
                "geo_alt='" + geo_alt + '\'' +
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
