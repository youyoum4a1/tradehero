package com.androidth.general.fragments.competition.zone.dto;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderPrizePoolDTO;
import com.androidth.general.models.number.THSignedNumber;

public class CompetitionZonePrizePoolDTO extends CompetitionZoneDTO
{
    @NonNull public final ProviderPrizePoolDTO providerPrizePoolDTO;
    @NonNull public final String currentPrizePool;
    @NonNull public final String nextPrizePool;
    @NonNull public final String playersNeeded;
    @NonNull public final String backgroundUrl;

    //<editor-fold desc="Constructors">
    public CompetitionZonePrizePoolDTO(
            @NonNull Resources resources,
            @NonNull ProviderPrizePoolDTO providerPrizePoolDTO)
    {
        super(null, null, null, R.drawable.default_image);
        this.providerPrizePoolDTO = providerPrizePoolDTO;

        currentPrizePool = providerPrizePoolDTO.current;
        nextPrizePool = resources.getString(R.string.provider_prize_pool_new_players_need,
                providerPrizePoolDTO.extra);
        playersNeeded = THSignedNumber.builder(providerPrizePoolDTO.newPlayerNeeded)
                .build().toString();
        backgroundUrl = providerPrizePoolDTO.background;
    }
    //</editor-fold>
}
