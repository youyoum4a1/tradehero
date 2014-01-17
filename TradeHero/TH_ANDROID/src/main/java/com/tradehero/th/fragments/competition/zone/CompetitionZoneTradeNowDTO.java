package com.tradehero.th.fragments.competition.zone;

/**
 * Created by xavier on 1/17/14.
 */
public class CompetitionZoneTradeNowDTO extends CompetitionZoneDTO
{
    public static final String TAG = CompetitionZoneTradeNowDTO.class.getSimpleName();

    public final String imageUrl;

    public CompetitionZoneTradeNowDTO(String title, String description, String imageUrl)
    {
        super(title, description);
        this.imageUrl = imageUrl;
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (imageUrl == null ? new Integer(0) : imageUrl).hashCode();
    }
}
