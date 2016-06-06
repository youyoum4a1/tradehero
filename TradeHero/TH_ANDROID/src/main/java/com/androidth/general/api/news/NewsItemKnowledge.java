package com.androidth.general.api.news;

import com.androidth.general.R;
import java.util.HashMap;

public class NewsItemKnowledge
{
    public static final int SEEKING_ALPHA_SOURCE_ID = 9;
    public static final int MOTLEY_FOOL_SOURCE_ID = 6;
    public static final int HECKYL_SOURCE_ID = 4;

    public static final HashMap<Integer, Integer> NEWS_PLACEHOLDER_MAP = new HashMap<>();
    static {
        NEWS_PLACEHOLDER_MAP.put(SEEKING_ALPHA_SOURCE_ID, R.drawable.seeking_alpha);
        NEWS_PLACEHOLDER_MAP.put(MOTLEY_FOOL_SOURCE_ID, R.drawable.motley_fool);
        NEWS_PLACEHOLDER_MAP.put(HECKYL_SOURCE_ID, R.drawable.icn_about_heckyl);
    }
}
