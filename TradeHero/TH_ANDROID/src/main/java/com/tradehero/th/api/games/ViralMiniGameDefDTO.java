package com.tradehero.th.api.games;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.KeyGenerator;

public class ViralMiniGameDefDTO implements DTO, KeyGenerator
{
    @JsonProperty("minigameId")
    public int viralMiniGameId;
    public String bannerUrl;
    public int bannerId;
    public String gameUrl;

    @Override public ViralMiniGameDefKey getDTOKey()
    {
        return new ViralMiniGameDefKey(viralMiniGameId);
    }
}
