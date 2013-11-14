package com.tradehero.th.api.competition;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 7:40 PM To change this template use File | Settings | File Templates. */
public class PrizeDTO
{
    public static final String TAG = PrizeDTO.class.getSimpleName();

    public int id;
    public String title;
    public String description;
    public int ordinalRank;
    public double amount;
    public String iconImageUrl;
    public String backgroundImageUrl;
    public String selectedBackgroundImageUrl;
    public double rowHeightPoint;
    public String expandedBackgroundImageUrl;
    public String expandedSelectedBackgroundImageUrl;
    public String prizeCcy;
}
