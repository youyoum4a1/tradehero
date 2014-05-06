package com.tradehero.th.fragments.competition.zone.dto;


public class CompetitionZoneTradeNowDTO extends CompetitionZoneDTO
{
    public static final String TAG = CompetitionZoneTradeNowDTO.class.getSimpleName();

    public final int imageResId;
    public final String imageUrl;

    public CompetitionZoneTradeNowDTO(String title, String description, int imageResId, String imageUrl)
    {
        super(title, description);
        this.imageResId = imageResId;
        this.imageUrl = imageUrl;
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^
                Integer.valueOf(imageResId).hashCode() ^
                (imageUrl == null ? Integer.valueOf(0) : imageUrl).hashCode();
    }

    @Override public String toString()
    {
        return "CompetitionZoneTradeNowDTO{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageResId=" + imageResId +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
