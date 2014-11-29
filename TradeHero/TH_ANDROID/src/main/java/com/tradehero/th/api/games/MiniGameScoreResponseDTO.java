package com.tradehero.th.api.games;

import com.tradehero.th.api.BaseResponseDTO;

public class MiniGameScoreResponseDTO extends BaseResponseDTO
{
    public int gameId;
    public String title;
    public String imageUrl;
    public int score;
    public int level;
    public double virtualDollars;
    public String displayHtmlText;

    @Override public String toString()
    {
        return "MiniGameScoreResponseDTO{" +
                "gameId=" + gameId +
                ", title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", score=" + score +
                ", level=" + level +
                ", virtualDollars=" + virtualDollars +
                ", displayHtmlText='" + displayHtmlText + '\'' +
                '}';
    }
}
